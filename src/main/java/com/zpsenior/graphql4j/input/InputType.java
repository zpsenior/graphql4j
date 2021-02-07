package com.zpsenior.graphql4j.input;

public interface InputType {

	public Object parseValue(String value)throws Exception;
}
