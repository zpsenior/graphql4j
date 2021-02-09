package com.zpsenior.graphql4j.schema;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import com.zpsenior.graphql4j.TypeConversion;
import com.zpsenior.graphql4j.annotation.Join;
import com.zpsenior.graphql4j.annotation.Variable;
import com.zpsenior.graphql4j.exception.ExecuteException;

public class Member{
	
	private AccessibleObject access;
	private Map<String, Integer> params;
	private String joinMethod;
	private String[] joinParams;
	
	public Member(AccessibleObject access) {
		this.access = access;
		if(access instanceof Method) {
			Method method = (Method)access;
			int i = 0;
			params = new HashMap<>();
			for(Parameter param : method.getParameters()) {
				Variable var = param.getAnnotation(Variable.class);
				params.put(var.value(), i);
				i++;
			};
		}else {
			Field field = (Field)access;
			Join join = field.getAnnotation(Join.class);
			if(join != null){
				joinMethod = join.bind();
				joinParams = join.params();
			}
		}
	}
	
	public Class<?> getValueType(){
		if(access instanceof Method) {
			return ((Method)access).getReturnType();
		}
		return ((Field)access).getType();
	}
	
	public AccessibleObject getAccess() {
		return access;
	}
	public boolean containsParam(String name) {
		return params.containsKey(name);
	}
	
	public boolean isMethod() {
		return (access instanceof Method);
	}
	
	public Object invoke(Object inst, Map<String, Object> paramValues)throws Exception {
		if(joinMethod != null) {
			throw new ExecuteException("join field(" + ((Field)access).getName() + ") can not be invoked");
		}
		if(access instanceof Method) {
			Method method = (Method)access;
			Parameter[] parameters = method.getParameters();
			Object[] values = new Object[params.size()];
			for(String name : params.keySet()) {
				Object value = paramValues.get(name);
				int idx = params.get(name);
				Class<?> paramClass = parameters[idx].getType();
				values[idx] = TypeConversion.conversion(paramClass, value);
			}
			return method.invoke(inst, values);
		}
		return ((Field)access).get(inst);
	}

	public String getJoinMethod() {
		return joinMethod;
	}

	public String[] getJoinParams() {
		return joinParams;
	}

	public String getName() {
		if(access instanceof Method) {
			Method method = (Method)access;
			return method.getName();
		}else {
			Field field = (Field)access;
			return field.getName();
		}
	}
	
	
	private String getTypeName(Type type) {
		if(type instanceof ParameterizedType) {
			ParameterizedType pt = (ParameterizedType)type;
			Class<?> cls = (Class<?>)pt.getActualTypeArguments()[0];
			return "[" + cls.getSimpleName() + "]";
		}
		return ((Class<?>)type).getSimpleName();
	}
	

	public String toString() {
		StringBuffer sb = new StringBuffer();
		if(access instanceof Method) {
			Method method = (Method)access;
			Parameter[] parameters = method.getParameters();
			sb.append(method.getName()).append("(");
			for(int i = 0; i < parameters.length; i++) {
				Parameter parameter = parameters[i];
				if(i > 0) {
					sb.append(", ");
				}
				Variable var = parameter.getAnnotation(Variable.class);
				sb.append(var.value()).append(":").append(getTypeName(parameter.getType()));
			}
			sb.append(")");
			Class<?> returnType = method.getReturnType();
			if(!"void".equals(returnType.getName())) {
				sb.append(":").append(getTypeName(method.getGenericReturnType()));
			}
		}else {
			Field field = (Field)access;
			sb.append(String.format("%-12s",field.getName())).append(" : ");
			sb.append(getTypeName(field.getGenericType()));
			if(joinMethod != null) {
				sb.append("  @join(").append(joinMethod).append("(");
				for(int i = 0; i < joinParams.length; i++) {
					if(i > 0) {
						sb.append(", ");
					}
					sb.append(joinParams[i]);
				}
				sb.append("))");
			}
		}
		return sb.toString();
	}
}
