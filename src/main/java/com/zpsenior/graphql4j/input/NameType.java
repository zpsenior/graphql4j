package com.zpsenior.graphql4j.input;

public class NameType extends InputType {
	
	private String name;
	private Class<?> bindClass;
	
	public NameType(String name, Class<?> bindClass) {
		this.name = name;
		this.bindClass = bindClass;
	}

	public String getName() {
		return name;
	}

	public Class<?> getBindClass() {
		return bindClass;
	}

	public void toString(StringBuffer sb) {
		sb.append(name);
	}

	@Override
	public boolean compatible(Class<?> cls) {
		return bindClass.isAssignableFrom(cls);
	}

	
}
