package com.zpsenior.graphql4j.schema;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
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
	
	
}
