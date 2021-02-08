package com.zpsenior.graphql4j.value;

import java.util.List;

import com.zpsenior.graphql4j.ql.QLContext;

public class ArrayValue extends Value {
	
	private Value[] array;
	
	public ArrayValue(List<Value> array) {
		this.array = array.toArray(new Value[array.size()]);
	}

	@Override
	public Object getValue(QLContext context) {
		Object[] values = new Object[array.length];
		int i = 0;
		for(Value val : this.array) {
			Object value = val.getValue(context);
			values[i] = value;
			i++;
		}
		return array;
	}

}
