package graphql4j.type;

import graphql4j.JObject;
import graphql4j.exception.TransformException;

public class Argument extends JObject implements Comparable<Argument>{
	
	private String name;
	private Type type;
	private String defaultValue;
	private boolean notNull;

	public Argument(String name, Type type, boolean notNull, String defaultValue)throws Exception {
		if(!(type instanceof Input)){
			throw new TransformException("type.must.be.scalar.type.or.input.type", type.getName());
		}
		this.name = name;
		this.type = type;
		this.notNull = notNull;
		if(defaultValue != null && !"".equals(defaultValue)){
			this.defaultValue = defaultValue;
		}
	}
	
	public String getName() {
		return name;
	}
	protected void setType(Type type){
		this.type = type;
	}
	public Type getType() {
		return type;
	}
	public String getDefaultValue() {
		return defaultValue;
	}
	public boolean isNotNull() {
		return notNull;
	}
	
	public boolean equals(Object o){
		if(!(o instanceof Argument)){
			return false;
		}
		Argument a = (Argument)o;
		if(!name.equals(a.name)){
			return false;
		}
		if(!type.equals(a.type)){
			return false;
		}
		if(notNull != a.notNull){
			return false;
		}
		return true;
	}

	public int compareTo(Argument o) {
		return name.compareTo(o.name);
	}
	@Override
	public void toString(StringBuffer sb) {
		sb.append(name);	
		sb.append(" : ");
		sb.append(type.getName());
		if(notNull){
			sb.append("!");
		}
		if(defaultValue != null && !"".equals(defaultValue)){
			sb.append(" = \"").append(defaultValue).append("\"");
		}
	}
	
}
