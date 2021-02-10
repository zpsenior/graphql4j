package com.zpsenior.graphql4j.schema;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zpsenior.graphql4j.annotation.Join;
import com.zpsenior.graphql4j.annotation.Variable;
import com.zpsenior.graphql4j.exception.ExecuteException;
import com.zpsenior.graphql4j.utils.ScalarUtils;

public class Member{
	
	public class Param{
		private int index;
		private Class<?> type;
		Param(int index, Class<?> type){
			this.index = index;
			this.type = type;
		}
		public int getIndex() {
			return index;
		}
		public Class<?> getType() {
			return type;
		}
	}
	
	private AccessibleObject access;
	private Map<String, Param> params;
	private Join join = null;
	private Class<?> valueType;
	private boolean scalarType = false;
	private boolean listType = false;
	
	public Member(AccessibleObject access) {
		this.access = access;
		if(access instanceof Method) {
			Method method = (Method)access;
			valueType = method.getReturnType();
			int i = 0;
			params = new HashMap<>();
			for(Parameter param : method.getParameters()) {
				Variable var = param.getAnnotation(Variable.class);
				params.put(var.value(), new Param(i, param.getType()));
				i++;
			};
		}else {
			Field field = (Field)access;
			valueType = field.getType();
			this.join = field.getAnnotation(Join.class);
		}
		scalarType = ScalarUtils.isScalarType(valueType);
		listType = valueType.isAssignableFrom(List.class);
	}
	
	public Class<?> getValueType(){
		return valueType;
	}
	
	public boolean isScalarType() {
		return scalarType;
	}
	
	public boolean isListType() {
		return listType;
	}

	public AccessibleObject getAccess() {
		return access;
	}
	
	public boolean isMethod() {
		return (access instanceof Method);
	}
	
	public Map<String, Param> getParams(){
		return params;
	}
	
	public Object invoke(Object inst, Map<String, Object> paramValues)throws Exception {
		if(join != null) {
			throw new ExecuteException("join field(" + ((Field)access).getName() + ") can not be invoked");
		}
		if(access instanceof Method) {
			Method method = (Method)access;
			Parameter[] parameters = method.getParameters();
			Object[] values = mapParamValues(paramValues, parameters);
			return method.invoke(inst, values);
		}
		return ((Field)access).get(inst);
	}

	private Object[] mapParamValues(Map<String, Object> paramValues, Parameter[] parameters) throws Exception {
		Object[] values = new Object[params.size()];
		for(String name : params.keySet()) {
			Object paramObject = paramValues.get(name);
			Param param = params.get(name);
			int idx = param.getIndex();
			Class<?> paramClass = parameters[idx].getType();
			if(!paramClass.isInstance(paramObject)) {
				
			}
			values[idx] = paramObject;
		}
		return values;
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
			sb.append(String.format("%-12s", field.getName())).append(" : ");
			sb.append(String.format("%-15s", getTypeName(field.getGenericType())));
			if(join != null) {
				sb.append("  @join(");
				sb.append(join.bind()).append("(");
				String[] params = join.params();
				for(int i = 0; i < params.length; i++) {
					if(i > 0) {
						sb.append(", ");
					}
					sb.append(params[i]);
				}
				sb.append("))");
			}
		}
		return sb.toString();
	}

	public Join getJoin() {
		return join;
	}
}
