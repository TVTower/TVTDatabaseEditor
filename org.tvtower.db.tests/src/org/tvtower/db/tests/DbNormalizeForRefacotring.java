package org.tvtower.db.tests;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Ignore;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.google.common.io.Files;

@Ignore
public class DbNormalizeForRefacotring {

	TreeMap<String, Node> persons = new TreeMap<>();
	TreeMap<String, Node> programmes = new TreeMap<>();
	TreeMap<String, Node> ads = new TreeMap<>();
	TreeMap<String, Node> news = new TreeMap<>();
	TreeMap<String, Node> scripts = new TreeMap<>();

	//allows defining a language tag that should be ignored for normalization
	//e.g. for comparing data base changes excluding changes in that language
	private static final String langToIgnore = null;// "pl"
	//do not change this constant!
	private static final String langIgnorePrefix = langToIgnore == null ? null : langToIgnore + " -";

	@Test
	public void iterateFolder() throws IOException {
		File parent = new File(".").getAbsoluteFile().getParentFile().getParentFile().getParentFile();
		File baseDir = new File(parent,"TVTower/res/database/default");

		Iterable<File> iterable = Files.fileTraverser().breadthFirst(baseDir);
		for (File file : iterable) {
			if(file.isFile() && file.getName().endsWith("xml") && ! file.getParentFile().getName().startsWith("lang")) {
				parseFile(file.getAbsolutePath(), "person", persons);
				parseFile(file.getAbsolutePath(), "programme", programmes);
				parseFile(file.getAbsolutePath(), "ad", ads);
				parseFile(file.getAbsolutePath(), "news", news);
				parseFile(file.getAbsolutePath(), "scripttemplate", scripts);
			}
		}

		String normName="normalized.txt";
		write(persons, "persons/"+normName);
		write(programmes, "progs/"+normName);
		write(ads, "ads/"+normName);
		write(news, "news/"+normName);
		write(scripts, "scripts/"+normName);
		printDiffs();
	}

	private void printDiffs() {
		try {
			MessageDigest hash= MessageDigest.getInstance("SHA-256");
			File parent=new File("target/dbcompare/");
			if(parent.exists() && parent.isDirectory()) {
				for (File subDir : parent.listFiles()) {
					if(subDir.exists() && subDir.isDirectory()) {
						File[] files = subDir.listFiles();
						if(files!=null && files.length==2){
							File f1 = files[0];
							File f2 = files[1];
							String b1 = new String(hash.digest(Files.toByteArray(f1)));
							String b2 =  new String(hash.digest(Files.toByteArray(f2)));
							if(!b1.equals(b2)) {
								System.err.println("diff in "+subDir.getName());
							}
						}
					}
				}
			}else {
				System.out.println("no compare directory found");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void normalizePersons() throws IOException {
		parsePersonFile("persons/db_orig.xml");
		write(persons, "persons/normOrig.txt");
		persons.clear();

		parsePersonFile("persons/db_new.xml");
		parsePersonFile("persons/db_new2.xml");
		write(persons, "persons/normNew.txt");
	}

	@Test
	public void normalizeProgrammes() throws IOException {
		parseProgrammeFile("progs/dborig1.xml");
		parseProgrammeFile("progs/dborig2.xml");
		write(programmes, "progs/normOrig.txt");
		programmes.clear();

		parseProgrammeFile("progs/dbnew1.xml");
		parseProgrammeFile("progs/dbnew2.xml");
		write(programmes, "progs/normNew.txt");
	}

	
	private void parsePersonFile(String fileName) {
		parseFile(fileName, "person", persons);
	}

	private void parseProgrammeFile(String fileName) {
		parseFile(fileName, "programme", programmes);
	}


	private void write(TreeMap<String, Node> map, String targetFile) throws IOException {
		String target="target/dbcompare/"+targetFile;
		File parent=new File(target).getParentFile();
		if(!parent.exists()) {
			parent.getParentFile().mkdir();
			parent.mkdir();
		}
		FileOutputStream f = new FileOutputStream(target);
		for (String id : map.keySet()) {
			f.write(toString(map.get(id)).getBytes());
			f.write("\n".getBytes());
		}
		f.flush();
		f.close();
	}

	public void parseFile(String fileName, String baseTag, TreeMap<String, Node> map) {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			InputSource is = new InputSource(new FileInputStream(new File(fileName)));

			Document doc = db.parse(is);
			NodeList nodes = doc.getElementsByTagName(baseTag);

			// iterate the employees
			for (int i = 0; i < nodes.getLength(); i++) {
				Node node = nodes.item(i);
				String id = ((Element) node).getAttribute("guid");
				if (map.containsKey(id)) {
					throw new IllegalStateException("duplicate id '" + id +"' in "+fileName );
				} else {
					map.put(id, node);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private String toString(Node n) {
		StringBuilder b = new StringBuilder(n.getNodeName());
		b.append(" - ");
		String nodeValue = n.getNodeValue();
		if (nodeValue != null && nodeValue.length() > 0) {
			b.append(nodeValue);
			b.append(" - ");
		}

		TreeMap<String, String> attMap = new TreeMap<>();
		NamedNodeMap attributes = n.getAttributes();
		if (attributes != null) {
			for (int i = 0; i < attributes.getLength(); i++) {
				Node att = attributes.item(i);
				String value = att.getNodeValue();
				if (value != null && value.length() > 0) {
					attMap.put(att.getNodeName(), value);
				}
			}
			if (!attMap.isEmpty()) {
				b.append("attributes: ");
				b.append(attMap);
			}
		}
		NodeList children = n.getChildNodes();
		if (children != null) {
			for (int i = 0; i < children.getLength(); i++) {
				Node child = children.item(i);
				if (child != null) {
					String childText = toString(child);
					if (childText != null && childText.length() > 0) {
						if (langToIgnore != null && childText.startsWith(langIgnorePrefix)) {
							i = i + 1;
						} else {
							b.append("; child: " + childText);
						}
					}
				}
			}
		}

		// System.out.println(attMap);
		return b.toString();
	}
}
