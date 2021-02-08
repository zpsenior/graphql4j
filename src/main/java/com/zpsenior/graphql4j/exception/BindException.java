package com.zpsenior.graphql4j.exception;

public class BindException extends Exception {

	private static final long serialVersionUID = -7830654612571546507L;
	
	public BindException(String msg, Object... args){
		super(msg);
	}

}
