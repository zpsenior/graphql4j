package com.zpsenior.graphql4j.ql;

import com.zpsenior.graphql4j.value.Value;

public class ElementArgument extends QLNode implements Comparable<ElementArgument>{
	
	private String name;
	private Value value;

	public ElementArgument(String name, Value value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}
	
	public Value getValue() {
		return value;
	}
	
	/*
	public void matchParameter(Entry entry, Class<?> paramType)throws Exception {
		if(value instanceof VariableValue) {
			entry.checkVariable((VariableValue)value, paramType);
		}else if(value instanceof ArrayValue) {
			if(!paramType.isArray()) {
				throw new BindException("argument(" + name + ")`s type is not array");
			}
		}else if(value instanceof ObjectValue) {
			if(paramType != Map.class) {
				throw new BindException("argument(" + name + ")`s type is not map");
			}
		}
	}*/

	@Override
	public int compareTo(ElementArgument target) {
		return name.compareTo(target.name);
	}

	public void toString(int deep, StringBuffer sb) {
		sb.append(name).append(":");
		value.toString(sb);
	}
}
