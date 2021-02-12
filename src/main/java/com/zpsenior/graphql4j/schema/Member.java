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
import com.zpsenior.graphql4j.exception.TypeException;
import com.zpsenior.graphql4j.ql.ElementArgument;
import com.zpsenior.graphql4j.ql.QLContext;
import com.zpsenior.graphql4j.utils.ScalarUtils;
import com.zpsenior.graphql4j.value.Value;

public class Member{
	
	private AccessibleObject access;
	private Value[] paramValues;
	private Join join = null;
	private Class<?> valueType;
	private boolean scalarType = false;
	private boolean listType = false;
	
	public Member(AccessibleObject access)throws Exception {
		this.access = access;
		this.join = access.getAnnotation(Join.class);
		if(access instanceof Method) {
			Method method = (Method)access;
			valueType = method.getReturnType();
			paramValues = new Value[method.getParameterCount()];
			Parameter[] parameters = method.getParameters();
			for(int i = 0; i < paramValues.length; i++) {
				Parameter param  = parameters[i];
				Variable var = param.getAnnotation(Variable.class);
				if(var == null) {
					throw new TypeException("lack variable annotation at parameter(" + i + ") in method:" + method.getName());
				}
			};
			if(join != null && join.params().length > 0) {
				throw new TypeException("join at method(" + method.getName() + ") can not set params property");
			}
		}else {
			Field field = (Field)access;
			valueType = field.getType();
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
	
	public void bindArgumentValues(ElementArgument[] arguments)throws Exception{
		if(!(access instanceof Method)) {
			return;
		}
		Method method = (Method)access;
		Parameter[] parameters = method.getParameters();
		for(ElementArgument arg : arguments) {
			if(!matchVarName(parameters, arg)) {
				throw new BindException("can not find argument(" + arg.getName() + ") in method:" + method.getName());
			}
		}
	}
	
	private boolean matchVarName(Parameter[] parameters, ElementArgument arg) throws Exception{
		String argName = arg.getName();
		for(int i = 0; i < parameters.length; i++) {
			Parameter param = parameters[i];
			Variable var = param.getAnnotation(Variable.class);
			if(argName.equals(var.value())) {
				Value val = arg.getValue();
				paramValues[i] = val;
				return true;
			}
		}
		return false;
	}
	
	public Object invoke(QLContext context, Object inst)throws Exception {
		if(access instanceof Method) {
			Method method = (Method)access;
			Object[] values = getParamValues(context, method.getParameters());
			if(join != null) {
				return context.call(join.bind(), values, valueType);
			}else {
				return method.invoke(inst, values);
			}
		}//is field
		if(join != null) {
			String[] names = join.params();
			Object[] values = new Object[names.length];
			for(int i = 0; i < names.length; i++) {
				Object val = PropertyUtils.getProperty(inst, names[i]);
				values[i] = val;
			}
			return context.call(join.bind(), values, valueType);
		}
		return ((Field)access).get(inst);
	}

	private Object[] getParamValues(QLContext context, Parameter[] parameters) throws Exception {
		Object[] values = new Object[paramValues.length];
		for(int i = 0; i < values.length; i++) {
			Value paramValue = paramValues[i];
			Object paramObject = paramValue.getValue(context);
			Class<?> paramClass = parameters[i].getType();
			if(!paramClass.isInstance(paramObject)) {
				BeanUtils.copyProperties(paramClass.newInstance(), paramObject);
			}
			values[i] = paramObject;
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
		return sb.toString();
	}

	public Join getJoin() {
		return join;
	}
}
