package org.tvtower.db.ui;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import org.eclipse.core.internal.content.TextContentDescriber;
import org.eclipse.core.runtime.content.IContentDescription;

//mark all xml files as invalid, prevent validation
public class TVTDatabaseContentTypeDescriber extends TextContentDescriber {
	@Override
	public int describe(Reader contents, IContentDescription description) throws IOException {
		return INVALID;
	}

	@Override
	public int describe(InputStream contents, IContentDescription description) throws IOException {
		return INVALID;
	}
}
