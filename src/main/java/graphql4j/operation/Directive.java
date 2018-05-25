package graphql4j.operation;

import java.util.Set;

import graphql4j.JObject;

public class Directive extends JObject implements Comparable<Directive> {
	
	public final static String DIRECTIVE_SKIP = "skip";
	public final static String DIRECTIVE_INCLUDE = "include";
	
	private String name;
	private Param[] params = new Param[0];
	
	public Directive(String name, Set<Param> params){
		this.name = name;
		if(params != null && params.size() > 0){
			this.params = params.toArray(new Param[params.size()]);
		}
	}

	public int compareTo(Directive o) {
		return name.compareTo(o.name);
	}

	public void toString(StringBuffer sb) {
		sb.append("@").append(name);
		sb.append("(");
		boolean first = true;
		for(Param param : params){
			if(!first){
				sb.append(", ");
			}
			param.toString(sb);
			first = false;
		}
		sb.append(")");
	}

	public final String getName() {
		return name;
	}

	public final Param[] getParams() {
		return params;
	}
}
