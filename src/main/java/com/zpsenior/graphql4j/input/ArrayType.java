package com.zpsenior.graphql4j.input;

public class ArrayType implements InputType {
	
	private InputType baseType;

	public ArrayType(InputType baseType) {
		this.baseType = baseType;
	}

	public InputType getBaseType() {
		return baseType;
	}

	@Override
	public Object parseValue(String value) {
		return null;
	}

}
