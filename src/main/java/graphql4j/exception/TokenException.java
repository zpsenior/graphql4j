package graphql4j.exception;

import graphql4j.Token;

public class TokenException extends BaseException {
	
	private static final long serialVersionUID = 886848722685224360L;

	public TokenException(String msg, Token token, Object... args){
		super(msg);
		logError(msg, token.getName() + "' at line:" + token.getLine() + ", row:" + token.getRow() + ", pos:" + token.getPos(), args);
	}

	public TokenException(String msg, char c, int line, int row, int pos){
		super(msg);
		logError(msg, c + "' at line:" + line + ", row:" + row + ", pos:" + pos);
	}

	public TokenException(String msg){
		super(msg);
	}
}
