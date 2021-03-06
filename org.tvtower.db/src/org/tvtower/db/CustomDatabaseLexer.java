package org.tvtower.db;

import org.antlr.runtime.CharStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.RecognizerSharedState;
import org.tvtower.db.parser.antlr.internal.InternalDatabaseLexer;

//lexer overriding adapted from the code I wrote for Xturtle (https://github.com/AKSW/Xturtle/blob/develop/de.itemis.tooling.xturtle/src/de/itemis/tooling/xturtle/CustomXturtleLexer.java)
public class CustomDatabaseLexer extends InternalDatabaseLexer {

	LexerOverrider overrider = new LexerOverrider();

	public CustomDatabaseLexer() {
		super();
	}

	public CustomDatabaseLexer(CharStream input) {
		this(input, new RecognizerSharedState());
	}

	public CustomDatabaseLexer(CharStream input, RecognizerSharedState state) {
		super(input, state);

	}

	@Override
	public void mTokens() throws RecognitionException {
		if (overrider.override(input, state)) {
			// done
		} else {
			super.mTokens();
		}
	}
}
