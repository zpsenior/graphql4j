package com.zpsenior.graphql4j.input;

public class NotNullType extends InputType {
	
	private InputType baseType;

	public NotNullType(InputType baseType) {
		this.baseType = baseType;
	}

	public InputType getBaseType() {
		return baseType;
	}

	public void toString(StringBuffer sb) {
		sb.append(baseType);
		sb.append("!");
	}

	@Override
	public boolean compatible(Class<?> cls) {
		return baseType.compatible(cls);
	}

}
