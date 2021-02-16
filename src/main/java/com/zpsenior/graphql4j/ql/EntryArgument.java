package com.zpsenior.graphql4j.ql;

import com.zpsenior.graphql4j.input.InputType;
import com.zpsenior.graphql4j.value.Value;

public class EntryArgument extends QLNode implements Comparable<EntryArgument>{
	
	private String name;
	
	private InputType type;
	
	private Value defaultValue;
	
	public EntryArgument(String name, InputType type, Value defaultValue) {
		this.name = name;
		this.type = type;
		this.defaultValue = defaultValue;
	}

	public String getName() {
		return name;
	}

	public InputType getType() {
		return type;
	}

	public Value getDefaultValue() {
		return defaultValue;
	}


	@Override
	public int compareTo(EntryArgument target) {
		return name.compareTo(target.name);
	}
	
	public void toString(int deep, StringBuffer sb) {
		sb.append("$").append(name).append(":");
		sb.append(type);
		if(defaultValue != null) {
			sb.append(" = ");
			defaultValue.toString(sb);
		}
	}
}
