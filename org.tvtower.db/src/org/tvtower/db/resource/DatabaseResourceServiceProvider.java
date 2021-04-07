package org.tvtower.db.resource;

import org.eclipse.emf.common.util.URI;
import org.eclipse.xtext.resource.impl.DefaultResourceServiceProvider;

public class DatabaseResourceServiceProvider extends DefaultResourceServiceProvider {
	//minimal change to prevent other xml files from being validated...
	//they are still being opened with the DatabaseEditor
	//https://git.eclipse.org/c/m2e/m2e-core.git/tree/org.eclipse.m2e.core/src/org/eclipse/m2e/core/internal/content/TextContentDescriber.java?id=fbb4f5902cade92cc1689e73209c60a6413a5ef7
	//https://dzone.com/articles/associating-xtext-editors-file	

	@Override
	public boolean canHandle(URI uri) {
		String extension = uri.fileExtension();
		if(extension!=null && "xml".equals(extension)){
			if(uri.toString().contains("/database/")) {
				return true;
			}
		}
		return false;
	}
}
