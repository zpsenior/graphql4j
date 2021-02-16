package com.zpsenior.graphql4j.schema;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.BeanUtils;

import com.zpsenior.graphql4j.annotation.Join;
import com.zpsenior.graphql4j.annotation.Variable;
import com.zpsenior.graphql4j.exception.BindException;
import com.zpsenior.graphql4j.exception.ExecuteException;
import com.zpsenior.graphql4j.exception.TypeException;
import com.zpsenior.graphql4j.ql.ElementArgument;
import com.zpsenior.graphql4j.ql.QLContext;
import com.zpsenior.graphql4j.utils.ScalarUtils;
import com.zpsenior.graphql4j.value.Value;

public class Member{
	
	private String name;
	private boolean isMethod;
	private String[] paramNames;
	private Class<?>[] paramClasses;
	private Value[] paramValues;
	private Join join = null;
	private Class<?> valueType;
	private Type valueGenericType;
	private boolean scalarType = false;
	private boolean listType = false;
	
	public Member(AccessibleObject access)throws Exception {
		this.isMethod = (access instanceof Method);
		this.join = access.getAnnotation(Join.class);
		if(isMethod) {
			Method method = (Method)access;
			name = method.getName();
			valueType = method.getReturnType();
			valueGenericType = method.getGenericReturnType();
			int paramCount = method.getParameterCount();
			paramNames  = new String[paramCount];
			paramClasses = new Class<?>[paramCount];
			paramValues  = new Value[paramCount];
			Parameter[] parameters = method.getParameters();
			for(int i = 0; i < paramCount; i++) {
				Parameter param  = parameters[i];
				Variable var = param.getAnnotation(Variable.class);
				if(var == null) {
					throw new TypeException("lack variable annotation at parameter(" + i + ") in method:" + method.getName());
				}
				paramNames[i] = var.value();
				paramClasses[i] = param.getType();
			};
			if(join != null && join.params().length > 0) {
				throw new TypeException("join at method(" + method.getName() + ") can not set params property");
			}
		}else {
			Field field = (Field)access;
			name = field.getName();
			valueType = field.getType();
			valueGenericType = field.getGenericType();
		}
		scalarType = ScalarUtils.isScalarType(valueType);
		listType = valueType.isAssignableFrom(List.class);
	}
	
	public Type getValueGenericType(){
		return valueGenericType;
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
	
	public boolean isMethod() {
		return isMethod;
	}
	
	public void bindArgumentValues(ElementArgument[] arguments)throws Exception{
		for(ElementArgument arg : arguments) {
			if(!matchVarName(arg)) {
				throw new BindException("can not find argument(" + arg.getName() + ") in method:" + name);
			}
		}
	}
	
	private boolean matchVarName(ElementArgument arg) throws Exception{
		String argName = arg.getName();
		for(int i = 0; i < paramNames.length; i++) {
			String paramName = paramNames[i];
			if(argName.equals(paramName)) {
				Value val = arg.getValue();
				paramValues[i] = val;
				return true;
			}
		}
		return false;
	}
	
	public Object invoke(QLContext context, Object inst)throws Exception {
		if(isMethod) {
			Object[] values = getParamValues(context);
			if(join != null) {
				return context.call(join.bind(), values, valueGenericType);
			}
			if(inst == null) {
				throw new ExecuteException("the instance invoking method(" + name + ") is null");
			}
			Method method = inst.getClass().getDeclaredMethod(name, paramClasses);
			return method.invoke(inst, values);
		}//is field
		if(join != null) {
			String[] names = join.params();
			Object[] values = new Object[names.length];
			for(int i = 0; i < names.length; i++) {
				Object val = PropertyUtils.getProperty(inst, names[i]);
				values[i] = val;
			}
			return context.call(join.bind(), values, valueGenericType);
		}
		if(inst == null) {
			throw new ExecuteException("the instance invoking field(" + name + ") is null");
		}
		Field field = inst.getClass().getDeclaredField(name);
		field.setAccessible(true);
		return field.get(inst);
		//return PropertyUtils.getProperty(inst, name);
	}

	private Object[] getParamValues(QLContext context) throws Exception {
		Object[] values = new Object[paramValues.length];
		for(int i = 0; i < paramValues.length; i++) {
			Value paramValue = paramValues[i];
			if(paramValue == null) {
				continue;
			}
			Object paramObject = paramValue.getValue(context);
			Class<?> paramClass = paramClasses[i];
			if(!paramClass.isInstance(paramObject)) {
				BeanUtils.copyProperties(paramClass.newInstance(), paramObject);
			}
			values[i] = paramObject;
		}
		return values;
	}

	public String getName() {
		return name;
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
		toString(sb);
		return sb.toString();
	}
	

	public void toString(StringBuffer sb) {
		if(isMethod) {
			sb.append(name).append("(");
			for(int i = 0; i < paramNames.length; i++) {
				String paramName = paramNames[i];
				if(i > 0) {
					sb.append(", ");
				}
				sb.append(paramName).append(":").append(getTypeName(paramClasses[i]));
			}
			sb.append(")");
			sb.append(":").append(getTypeName(valueGenericType));
		}else {
			sb.append(String.format("%-12s", name)).append(" : ");
			sb.append(String.format("%-15s", getTypeName(valueGenericType)));
		}
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

	public Join getJoin() {
		return join;
	}
}
