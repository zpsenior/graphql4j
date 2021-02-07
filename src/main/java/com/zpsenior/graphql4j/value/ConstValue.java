package com.zpsenior.graphql4j.value;

import com.zpsenior.graphql4j.ql.QLContext;

public class ConstValue extends Value {
	
	private Object value;
	
	public ConstValue(Object value) {
		if(value != null) {
			
		}
		this.value = value;
	}

	@Override
	public Object getValue(QLContext context) {
		return value;
	}

}
