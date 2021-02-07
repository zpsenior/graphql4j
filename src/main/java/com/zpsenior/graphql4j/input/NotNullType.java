package com.zpsenior.graphql4j.input;

public class NotNullType implements InputType {
	
	private InputType baseType;

	public NotNullType(InputType baseType) {
		this.baseType = baseType;
	}

	public InputType getBaseType() {
		return baseType;
	}

	@Override
	public Object parseValue(String value)throws Exception {
		if(value == null) {
			throw new RuntimeException("value can not be null");
		}
		return baseType.parseValue(value);
	}

}
