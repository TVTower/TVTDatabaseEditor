package org.tvtower.db;

import org.antlr.runtime.CharStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.RecognizerSharedState;
import org.tvtower.db.parser.antlr.internal.InternalDatabaseLexer;

//lexer overriding adapted from the code I wrote for Xturtle (https://github.com/AKSW/Xturtle/blob/develop/de.itemis.tooling.xturtle/src/de/itemis/tooling/xturtle/CustomXturtleLexer.java)
public class CustomDatabaseLexer extends InternalDatabaseLexer {

	LexerOverrider overrider = new LexerOverrider();
	private boolean override=true;

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
	public void setCharStream(CharStream input) {
		override=true;
		//AbstractLexerBasedConverter#getTokenSource is used to parse single tokens for code completion
		//overriding the standard lexer for those cases causes code completion for references within Strings to fail
		//so we assume that if the input starts with = or " we are in a code completion scenario
		if (input.size() > 0) {
			char firstChar = input.substring(0, 0).charAt(0);
			if (firstChar == '=' || firstChar == '"') {
				override = false;
			}
		}
		super.setCharStream(input);
	}

	@Override
	public void mTokens() throws RecognitionException {
		if (override && overrider.override(input, state)) {
			// done
		} else {
			super.mTokens();
		}
	}
}
