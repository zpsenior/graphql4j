package com.zpsenior.graphql4j.parser;

public class Token {
	
	public static final Token END = new Token(null, TokenType.TOKEN_TYPE_END, null);

	private String content;
	private TokenType type;
	private Position pos;

	public Token(String content, TokenType type, Position pos) {
		this.content = content;
		this.type = type;
		this.pos = pos;
	}
	
	public String getContent() {
		return content;
	}

	public TokenType getType() {
		return type;
	}

	public int getPos() {
		return pos.getPos();
	}

	public int getCol() {
		return pos.getCol();
	}

	public int getRow() {
		return pos.getRow();
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		//sb.append(pos);
		sb.append("->【").append(content).append("】");
		return sb.toString();
	}
}
