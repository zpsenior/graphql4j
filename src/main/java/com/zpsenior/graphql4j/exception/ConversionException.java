package com.zpsenior.graphql4j.exception;

public class ConversionException extends Exception {

	private static final long serialVersionUID = -7830654612571546507L;
	
	public ConversionException(String msg, Object... args){
		super(msg);
	}

}
