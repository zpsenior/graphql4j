package com.zpsenior.graphql4j.input;


public class ArrayType implements InputType {
	
	private InputType baseType;

	public ArrayType(InputType baseType) {
		this.baseType = baseType;
	}

	public InputType getBaseType() {
		return baseType;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		sb.append(baseType);
		sb.append("]");
		return sb.toString();
	}

	@Override
	public boolean compatible(Class<?> cls) {
		if(cls.isArray()){
			return baseType.compatible(cls.getComponentType());
		}
		return false;
	}
}
