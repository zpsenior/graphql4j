package graphql4j.exception;

import graphql4j.Token;

public class TokenException extends Exception {
	
	private static final long serialVersionUID = 886848722685224360L;

	public TokenException(String msg, Token token, Object... args){
		super(msg);
		System.out.println(msg + ", '" + token.getName() + "' at line:" + token.getLine() + ", row:" + token.getRow() + ", pos:" + token.getPos());
	}

	public TokenException(String msg, char c, int line, int row, int pos){
		super(msg);
		System.out.println(msg + ", '" + c + "' at line:" + line + ", row:" + row + ", pos:" + pos);
	}

	public TokenException(String msg){
		super(msg);
	}
}
