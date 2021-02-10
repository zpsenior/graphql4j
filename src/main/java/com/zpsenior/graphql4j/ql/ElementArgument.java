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

	public Object calculateValue(QLContext context) {
		return value.getValue(context);
	}
	
	public Value getValue() {
		return value;
	}

	@Override
	public int compareTo(ElementArgument target) {
		return name.compareTo(target.name);
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(name).append(":");
		sb.append(value);
		return sb.toString();
	}
}
