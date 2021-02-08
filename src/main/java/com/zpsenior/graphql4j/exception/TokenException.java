package com.zpsenior.graphql4j.exception;

public class TokenException extends Exception {

	private static final long serialVersionUID = -7830654612571546507L;
	
	public TokenException(String msg, Object... args){
		super(msg);
	}

}
