package com.zpsenior.graphql4j.exception;

public class ExecuteException extends Exception {

	private static final long serialVersionUID = -7830654612571546507L;
	
	public ExecuteException(String msg, Object... args){
		super(msg);
	}

}
