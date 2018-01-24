package graphql4j.operation;

import graphql4j.JObject;
import graphql4j.exception.TransformException;
import graphql4j.type.Input;
import graphql4j.type.Type;

public class QueryArgument extends JObject implements Comparable<QueryArgument>{
	
	private String name;
	private Type type;
	private ParamValue pvDefault;
	private boolean notNull;

	public QueryArgument(String name, Type type, boolean notNull, ParamValue pvDefault)throws Exception {
		if(!(type instanceof Input)){
			throw new TransformException("type.must.be.scalar.type.or.input.type", type.getName());
		}
		this.name = name;
		this.type = type;
		this.notNull = notNull;
		this.pvDefault = pvDefault;
		if(pvDefault != null){
			this.defaultValue = pvDefault.getValue(type);
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
	public ParamValue getDefaultValue() {
		return pvDefault;
	}
	public boolean isNotNull() {
		return notNull;
	}

	@Override
	public int compareTo(QueryArgument o) {
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
		if(defaultValue != null){
			sb.append(" = ");
			pvDefault.toString(sb);
		}
	}

	private Object defaultValue;
	private Object value;

	public Object getValue()throws Exception{
		if(value != null){
			return value;
		}
		if(defaultValue != null){
			return defaultValue;
		}
		throw new TransformException("not.set.query.argument", name);
	}

	public void bindValue(Object value)throws Exception{
		if(value != null){
			this.value = ((Input)type).parseValue(value);
		}
	}

	public void clearValue(){
		this.value = null;
	}
}
