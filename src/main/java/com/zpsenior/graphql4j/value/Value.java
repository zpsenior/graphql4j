package com.zpsenior.graphql4j.value;

import com.zpsenior.graphql4j.ql.QLContext;

public abstract class Value {

	public abstract Object getValue(QLContext context);
	
	public abstract void toString(StringBuffer sb);
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		toString(sb);
		return sb.toString();
	}
	
}
