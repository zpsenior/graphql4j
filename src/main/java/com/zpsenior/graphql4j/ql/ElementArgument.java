package com.zpsenior.graphql4j.ql;

import com.zpsenior.graphql4j.value.Value;

public class ElementArgument implements Comparable<ElementArgument>{
	
	private String name;
	private Value value;

	public ElementArgument(String name, Value value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public Object getValue(QLContext context) {
		return value.getValue(context);
	}
	
	@Override
	public int compareTo(ElementArgument target) {
		return name.compareTo(target.name);
	}
	
}
