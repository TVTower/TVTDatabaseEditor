package org.tvtower.db.tests

import com.google.inject.Inject
import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.util.ArrayList
import java.util.HashMap
import java.util.List
import java.util.Map
import java.util.TreeMap
import org.eclipse.emf.ecore.EObject
import org.eclipse.xtext.EcoreUtil2
import org.eclipse.xtext.nodemodel.util.NodeModelUtils
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.extensions.InjectionExtension
import org.eclipse.xtext.testing.util.ParseHelper
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.^extension.ExtendWith
import org.tvtower.db.database.Database
import org.tvtower.db.database.Programme
import org.tvtower.db.database.Programmes
import org.junit.Ignore

@ExtendWith(InjectionExtension)
@InjectWith(DatabaseInjectorProvider)
//helper for splitting the large database programme file
//into smaller files with identical content, each file is self contained
//(no trigger references to files other files)
//the file name includes the year for loading the file (i.e. 1995
//means no loading before 1995 necessary)
class ProgrammesSplitter {
	@Inject
	ParseHelper<Database> parseHelper

	/**
	 * list of programmes for the file of the year
	 * */
	Map<Integer, List<Programme>> progByYear = new TreeMap
	/**
	 * GUID->Programme
	 */
	Map<String, Programme> progById = new HashMap
	/**
	 * GUID->release year
	 */
	Map<String, Integer> yearForProg = new HashMap

	@Test
	@Ignore
	def void checkReleaseYearBucket() {
		(1975 .. 2020).forEach[println(it + " " + yearBucket(it))]
	}

	@Test
	@Ignore
	def void split() {
		// load programmes
		val parent = new File(".").getAbsoluteFile().getParentFile().getParentFile().getParentFile();
		val baseDir = new File(parent, "TVTower/res/database/default");
		val programmesXml = new File(baseDir, "database_programmes.xml");
		val model = parseHelper.parse(new String(Files.readAllBytes(programmesXml.toPath)));
		val programmes = EcoreUtil2.getAllContentsOfType(model, Programmes).get(0);
		// duplicate check, trigger analysis, store release year
		val triggers = new ProgTriggerHandler
		programmes.programmes.forEach [ p |
			if (progById.putIfAbsent(p.name, p) !== null) {
				throw new IllegalStateException("duplicate guid")
			}
			triggers.add(p)
			var baseYear = p.baseYear
			yearForProg.put(p.name, baseYear)
		]

		// place programme in bucket depending on "trigger group"
		programmes.programmes.forEach [ p |
			val int year = triggers.getMinimalYear(p, yearForProg)
			progByYear.computeIfAbsent(year, [k|new ArrayList]).add(p)
		]

		// write file per bucket 
		val target = new File("target/progs")
		target.listFiles.forEach[f|f.delete]
		target.delete
		target.mkdir
		progByYear.forEach [ k, v |
			writeProgFile(k, v)
			println("year" + k + " count " + v.size)
		]
	}

	def Integer baseYear(Programme p) {
		var year = p.data.year
		if (year.isNullOrEmpty && p.releaseTime !== null) {
			year = p.releaseTime.year
			if (year.isNullOrEmpty) {
				// fallback - probably relative release year
				year = "1980"
			}
		}
		var Integer yearInt = Integer.parseInt(year)
		//1980 is earliest start year
		if (yearInt < 1980) {
			yearInt = 1980
		}
		return yearBucket(yearInt)
	}

	def int yearBucket(int yearInt) {
		// bucket for ever 5 years
		return 10 * (yearInt / 10) + 5 * Math.round(0.1 * (yearInt % 10)) as int;
	}

	def void writeProgFile(int year, List<Programme> progs) {
		Files.write(new File("target/progs/database_programmes_" + year + ".xml").toPath,
		'''
			<?xml version="1.0" encoding="utf-8"?>
			<tvtdb>
				<version value="3" />
				<allprogrammes>«FOR p : progs»
				«p.toTxt»
				«ENDFOR»
				</allprogrammes>
				</tvtdb>
		'''.toString().getBytes(StandardCharsets.UTF_8))
	}

	def String toTxt(EObject o) {
		//retrieve input text from original file (including indentation and comments) 
		NodeModelUtils.getNode(o).text
	}
}
