package graphql4j.operation;

import graphql4j.JObject;

public class Param extends JObject implements Comparable<Param>{
	private String name;
	private ParamValue value;
	
	public Param(String name, ParamValue value){
		this.name = name;
		this.value = value;
	}
	
	@Override
	public int compareTo(Param o) {
		return name.compareTo(o.name);
	}

	public String getName() {
		return name;
	}

	public ParamValue getParamValue() {
		return value;
	}

	@Override
	public void toString(StringBuffer sb) {
		sb.append(name);
		if(value == null){
			return;
		}
		sb.append(":");
		value.toString(sb);
	}
}
