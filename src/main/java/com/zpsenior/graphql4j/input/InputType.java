package com.zpsenior.graphql4j.input;

public abstract class InputType {

	public abstract boolean compatible(Class<?> cls);
	
	public abstract void toString(StringBuffer sb);
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		toString(sb);
		return sb.toString();
	}
}
