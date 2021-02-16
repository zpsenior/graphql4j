package com.zpsenior.graphql4j.input;


public class ArrayType extends InputType {
	
	private InputType baseType;

	public ArrayType(InputType baseType) {
		this.baseType = baseType;
	}

	public InputType getBaseType() {
		return baseType;
	}

	public void toString(StringBuffer sb) {
		sb.append("[");
		sb.append(baseType);
		sb.append("]");
	}

	@Override
	public boolean compatible(Class<?> cls) {
		if(cls.isArray()){
			return baseType.compatible(cls.getComponentType());
		}
		return false;
	}
}
