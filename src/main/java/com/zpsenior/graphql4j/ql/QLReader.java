package com.zpsenior.graphql4j.ql;

import java.io.Reader;
import java.io.StringReader;

import com.zpsenior.graphql4j.exception.TokenException;
import com.zpsenior.graphql4j.parser.Token;
import com.zpsenior.graphql4j.parser.TokenParser;
import com.zpsenior.graphql4j.parser.TokenType;

public class QLReader {
	

	private int currentPos = -1;
	
	private Token[] tokens;
	

	public QLReader(String ql)throws Exception {
		this(new StringReader(ql));
	}
	
	public QLReader(Reader reader)throws Exception {
		TokenParser parser = new TokenParser(
				new String[] {
						"(", ")", "[", "]", "{", "}", "$", "!", ":", "=", ","
				},
				new String[] {"query", "mutation", "subscription"}
		);
		parser.parser(reader);
		
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
			logout("checkPunctuator " + t);
			return true;
		}
		forward(-1);
		return false;
	}

	public void readPunctuator(String symbol)throws Exception {
		Token t = readToken();
		if(t.getContent().equals(symbol) && TokenType.TOKEN_TYPE_PUNCTUATOR == t.getType()){
			logout("readPunctuator " + t);
			return ;
		}
		throw new TokenException("expect " + symbol + ", but token:" + t);
	}

	public String readName() throws Exception{
		Token t = readToken();
		if(TokenType.TOKEN_TYPE_NAME == t.getType()){
			logout("readName " + t);
			return t.getContent();
		}
		throw new TokenException("expect name type, but token:" + t);
	}

	public boolean lookName()throws Exception {
		Token t = readToken();
		forward(-1);
		if(TokenType.TOKEN_TYPE_NAME == t.getType()){
			logout("lookName " + t);
			return true;
		}
		return false;
	}

	public boolean checkKeyword(String name)throws Exception {
		Token t = readToken();
		if(t.getContent().equals(name) && TokenType.TOKEN_TYPE_KEYWORD == t.getType()){
			logout("checkKeyword " + t);
			return true;
		}
		forward(-1);
		return false;
	}

	public boolean eof()throws Exception {
		Token t = readToken();
		forward(-1);
		if(TokenType.TOKEN_TYPE_END == t.getType()){
			logout("eof");
			return true;
		}
		return false;
	}
	
	private void logout(String msg) {
		System.out.println(msg);
	}
}
