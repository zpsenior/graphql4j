package com.zpsenior.graphql4j.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TokenParser extends TextParser {
	
	private Set<String> keywords = null;
	
	private Set<String> punctuators = null;
	
	public TokenParser(String[] punctuators, String[] keywords) {
		this.punctuators = new HashSet<>(Arrays.asList(punctuators));
		this.keywords = new HashSet<>(Arrays.asList(keywords));
	}
	
	private List<Token> tokens = new ArrayList<>();

	@Override
	protected void conversion(Word word) {
		String content = word.getContent();
		Token token;
		TokenType type;
		if("".equals(content.trim())) {
			return;
		}
		if(keywords.contains(content)) {
			type = TokenType.TOKEN_TYPE_KEYWORD;
		}else if(punctuators.contains(content)) {
			type = TokenType.TOKEN_TYPE_PUNCTUATOR;
		}else {
			if(content.startsWith("\"") && content.endsWith("\"")) {
				type = TokenType.TOKEN_TYPE_QUOTE;
				content = content.substring(1, content.length() - 1);
			}else {
				type = TokenType.TOKEN_TYPE_NAME;
			}
		}
		token = new Token(content, type, word.getPos());
		tokens.add(token);
	}
	
	public Token[] getTokens() {
		return tokens.toArray(new Token[tokens.size()]);
	}

}
