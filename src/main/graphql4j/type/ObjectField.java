package graphql4j.type;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import graphql4j.JObject;
import graphql4j.exception.BindException;
import graphql4j.exception.TransformException;

public class ObjectField extends JObject implements Comparable<ObjectField>{
	private String name;
	private Method method;
	private List<Argument> arguments;
	private Type type;
	private boolean notNull;
	
	public ObjectField(String name, Type type, Method method, List<Argument> arguments, boolean notNull){
		this.name = name;
		this.type = type;
		this.method = method;
		this.arguments = arguments;
		this.notNull = notNull;
	}
	
	
	public String getName() {
		return name;
	}
	
	public final Method getMethod() {
		return method;
	}

	public boolean isNotNull() {
		return notNull;
	}

	public Argument[] getArguments() {
		if(arguments == null || arguments.size() <= 0){
			return null;
		}
		return arguments.toArray(new Argument[arguments.size()]);
	}
	
	public Argument getArgument(String name){
		if(arguments == null || arguments.size() <= 0){
			return null;
		}
		for(Argument arg : arguments){
			if(arg.getName().equals(name)){
				return arg;
			}
		}
		return null;
	}
	
	public Object invokeMethod(Object obj, Map<String, ?> params)throws Exception{
		Object[] methodParams = getMethodParams(params);
		Object res;
		int len = method.getParameterTypes().length;
		if(len == 0){
			res = method.invoke(obj);
		}else{
			if(len != methodParams.length){
				throw new BindException("diff.method.param.length");
			}
			res = method.invoke(obj, methodParams);
		}
		return res;
	}
	
	public Object[] getMethodParams(Map<String, ?> params)throws Exception{
		List<Object> list = new ArrayList<Object>();
		if(arguments != null){
			for(Argument arg : arguments){
				String name = arg.getName();
				if(!params.containsKey(name)){
					throw new TransformException("not.set.value.of.name", name);
				}
				Object value = params.get(name);
				list.add(value);
			}
		}
		return list.toArray();
	}
	
	public Type getType() {
		return type;
	}
	
	protected void setType(Type type){
		this.type = type;
	}

	@Override
	public int compareTo(ObjectField o) {
		return name.compareTo(o.name);
	}

	public boolean equals(Object o){
		if(!(o instanceof ObjectField)){
			return false;
		}
		ObjectField of = (ObjectField)o;
		if(!of.name.equals(name)){
			return false;
		}
		if(!of.method.getName().equals(method.getName())){
			return false;
		}
		if(!of.type.equals(type)){
			return false;
		}
		if(!compareArgument(of.arguments, this.arguments)){
			return false;
		};
		return true;
	}
	
	private boolean compareArgument(List<Argument> args1, List<Argument> args2) {
		if(args1 == null && args2 == null){
			return true;
		}
		if(args1.size() != args2.size()){
			return false;
		}
		for(Argument arg1 : args1){
			for(Argument arg2 : args2){
				if(!arg1.equals(arg2)){
					return false;
				}
			}
		}
		return true;
	}


	@Override
	public void toString(StringBuffer sb) {
		if(method != null){
			sb.append("@bind(\"");
			sb.append(method.getName());
			sb.append("\")");
			sb.append("\n   ");
		}
		sb.append(name);
		if(arguments != null){
			sb.append(" (");
			boolean first = true;
			for(Argument arg : arguments){
				if(!first){
					sb.append(",");
				}
				arg.toString(sb);
				first = false;
			}
			sb.append(")");
		}
		sb.append(" : ");
		sb.append(type.getName());
		
	}
}
