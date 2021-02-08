package com.zpsenior.graphql4j.input;

public class NotNullType implements InputType {
	
	private InputType baseType;

	public NotNullType(InputType baseType) {
		this.baseType = baseType;
	}

	public InputType getBaseType() {
		return baseType;
	}

}
