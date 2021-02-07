package com.zpsenior.graphql4j.input;

public class NameType implements InputType {
	
	private String name;
	
	public NameType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@Override
	public Object parseValue(String value) {
		// TODO Auto-generated method stub
		return null;
	}

}
