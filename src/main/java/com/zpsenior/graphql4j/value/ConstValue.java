package com.zpsenior.graphql4j.value;

import com.zpsenior.graphql4j.ql.QLContext;

public class ConstValue extends Value {
	
	private Object value;
	
	public ConstValue(String value) {
		if(value == null) {
			this.value = value;
		}
		if("true".equalsIgnoreCase(value)||"false".equalsIgnoreCase(value)) {
			this.value = new Boolean(value);
		}else if("null".equalsIgnoreCase(value)) {
			this.value = null;
		}else if(value.startsWith("\"")&& value.endsWith("\"")){
			this.value = value.substring(1, value.length() - 1);
		}else {
			
		}
	}

	@Override
	public Object getValue(QLContext context) {
		return value;
	}

}
