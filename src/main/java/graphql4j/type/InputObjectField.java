package graphql4j.type;

import java.lang.reflect.Method;

import graphql4j.JObject;
import graphql4j.exception.TransformException;

public class InputObjectField extends JObject implements Comparable<InputObjectField>{
	
	private String name;
	private Type type;
	private String defaultValue;
	private boolean notNull;
	
	private Method method;

	public InputObjectField(String name, Method method, Type type, boolean notNull, String defaultValue)throws Exception {
		if(!(type instanceof Input)){
			throw new TransformException("type.must.be.scalar.type.or.input.type", type.getName());
		}
		this.name = name;
		if(method == null){
			throw new TransformException("not.bind.method");
		}
		this.method = method;
		this.type = type;
		this.notNull = notNull;
		this.defaultValue = defaultValue;
	}
	
	public String getName() {
		return name;
	}
	public final Method getMethod() {
		return method;
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
	
	public void invokeSet(Object obj, Object params)throws Exception{
		method.invoke(obj, params);
	}

	public int compareTo(InputObjectField o) {
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
