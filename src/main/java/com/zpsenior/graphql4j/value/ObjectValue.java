package com.zpsenior.graphql4j.value;

import java.util.HashMap;
import java.util.Map;

import com.zpsenior.graphql4j.ql.QLContext;

public class ObjectValue extends Value {
	
	private Map<String, Value> map;
	
	public ObjectValue(Map<String, Value> map) {
		this.map = new HashMap<>(map);
	}

	@Override
	public Object getValue(QLContext context) {
		Map<String, Object> values = new HashMap<>();
		for(String name : map.keySet()) {
			Value val = map.get(name);
			Object value = val.getValue(context);
			values.put(name, value);
		}
		return values;
	}
	
	public Value[] getValues() {
		return map.values().toArray(new Value[map.size()]);
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		boolean first = true;
		sb.append("{");
		for(String key : map.keySet()) {
			if(first) {
				first = false;
			}else {
				sb.append(", ");
			}
			Value val = map.get(key);
			sb.append(key).append(":").append(val);
		}
		sb.append("}");
		return sb.toString();
	}
}
