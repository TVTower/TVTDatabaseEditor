package org.tvtower.db;

import org.antlr.runtime.BaseRecognizer;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.RecognizerSharedState;

public class LexerOverrider {

	private static final int CHANNEL = BaseRecognizer.DEFAULT_TOKEN_CHANNEL;
	private int anyOuterRule=CustomDatabaseLexer.RULE_ANY_OTHER;

	public boolean override(CharStream input, RecognizerSharedState state) {
		return overrideSingleDoubleQuote(input, state);
	}

	private void stateOK(RecognizerSharedState state, int rule) {
		state.type = rule;
		state.channel = CHANNEL;
		state.failed = false;
		state.backtracking = 0;
	}


	//" as inches after a number...
	private boolean overrideSingleDoubleQuote(CharStream input, RecognizerSharedState state) {
		if(input.LA(1)=='"'){
			int previous=input.LA(-1);
			if(previous>='0' && previous<='9') {
				input.consume();
				stateOK(state, anyOuterRule);
				return true;
			}
		}
		return false;
	}
}
