package com.zpsenior.graphql4j.ql;

import java.util.Map;

import com.zpsenior.graphql4j.exception.BindException;
import com.zpsenior.graphql4j.schema.Member.Param;
import com.zpsenior.graphql4j.value.ArrayValue;
import com.zpsenior.graphql4j.value.ObjectValue;
import com.zpsenior.graphql4j.value.Value;
import com.zpsenior.graphql4j.value.VariableValue;

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
	
	public Value getValue() {
		return value;
	}
	
	public void matchParameter(Entry entry, Param param)throws Exception {
		Class<?> paramType = param.getType();
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
