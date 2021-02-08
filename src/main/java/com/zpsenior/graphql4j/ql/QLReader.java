package com.zpsenior.graphql4j.ql;

import java.io.StringReader;

import com.zpsenior.graphql4j.exception.TokenException;
import com.zpsenior.graphql4j.parser.Token;
import com.zpsenior.graphql4j.parser.TokenParser;
import com.zpsenior.graphql4j.parser.TokenType;

public class QLReader {
	

	private int currentPos = -1;
	
	private Token[] tokens;
	
	public QLReader(String ql)throws Exception {
		TokenParser parser = new TokenParser(
				new String[] {
						"(", ")", "[", "]", "{", "}", "$", "!", ":", "="
				},
				new String[] {"query", "mutation", "subscription"}
		);
		parser.parser(new StringReader(ql));
		
		tokens = parser.getTokens();
	}
	
	private void forward(int deep){
		int pos = currentPos + deep;
		if(pos >= tokens.length|| pos < -1){
			return;
		}
		currentPos = pos;
	}
	
	public Token lookahead(int deep){
		int pos = currentPos + deep;
		if(pos >= tokens.length|| pos < 0){
			return null;
		}
		return tokens[pos];
	}
	
	protected Token read(){
		Token t = lookahead(1);
		if(t != null){
			currentPos++;
		}
		return t;
	}
	
	public Token readToken()throws Exception{
		Token token = read();
		if(token == null){
			return Token.END;
		}
		return token;
	}

	public boolean checkPunctuator(String symbol) throws Exception {
		Token t = readToken();
		if(t.getContent().equals(symbol) && TokenType.TOKEN_TYPE_PUNCTUATOR == t.getType()){
			//log.debug(t.getPos() + ":" + t.getName());
			return true;
		}
		forward(-1);
		return false;
	}

	public void readPunctuator(String symbol)throws Exception {
		Token t = readToken();
		if(t.getContent().equals(symbol) && TokenType.TOKEN_TYPE_PUNCTUATOR == t.getType()){
			return ;
		}
		throw new TokenException("unexpect.token", t);
	}

	public String readName() throws Exception{
		Token t = readToken();
		if(TokenType.TOKEN_TYPE_NAME == t.getType()){
			return t.getContent();
		}
		throw new TokenException("unexpect.token", t);
	}

	public boolean lookName()throws Exception {
		Token t = readToken();
		forward(-1);
		if(TokenType.TOKEN_TYPE_NAME == t.getType()){
			return true;
		}
		return false;
	}

	public boolean checkName(String name)throws Exception {
		Token t = readToken();
		if(t.getContent().equals(name) && TokenType.TOKEN_TYPE_NAME == t.getType()){
			return true;
		}
		forward(-1);
		return false;
	}

}
