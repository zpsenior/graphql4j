package com.zpsenior.graphql4j.exception;

public class CompileException extends Exception {

	private static final long serialVersionUID = -7830654612571546507L;
	
	public CompileException(String msg, Object... args){
		super(msg);
	}

}
