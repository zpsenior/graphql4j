package com.zpsenior.graphql4j.exception;

public class TypeException extends Exception {

	private static final long serialVersionUID = -7830654612571546507L;
	
	public TypeException(String msg, Object... args){
		super(msg);
	}

}
