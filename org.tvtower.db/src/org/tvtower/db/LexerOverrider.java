package org.tvtower.db;

import org.antlr.runtime.BaseRecognizer;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.RecognizerSharedState;

public class LexerOverrider {

	private static final int CHANNEL = BaseRecognizer.DEFAULT_TOKEN_CHANNEL;
	private int anyOuterRule=CustomDatabaseLexer.RULE_ANY_OTHER;
	private boolean outsideTag=true;

	public boolean override(CharStream input, RecognizerSharedState state) {
		//check inside/outside tag
		int i=input.LA(-1);
		if(i=='<') {
			outsideTag=false;
		}else if(i=='>') {
			outsideTag=true;
		}

		//outside an xml tag there are no strings
		//handle quotes as plain text content
		if (outsideTag) {
			i = input.LA(1);
			if (i == '"' || i == '\'') {
				input.consume();
				stateOK(state, anyOuterRule);
				return true;
			}
		}
		return false;
	}

	private void stateOK(RecognizerSharedState state, int rule) {
		state.type = rule;
		state.channel = CHANNEL;
		state.failed = false;
		state.backtracking = 0;
	}
}
