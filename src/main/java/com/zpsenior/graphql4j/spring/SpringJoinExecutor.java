package com.zpsenior.graphql4j.spring;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.ApplicationContext;

import com.zpsenior.graphql4j.JoinExecutor;
import com.zpsenior.graphql4j.exception.BindException;
import com.zpsenior.graphql4j.exception.ExecuteException;

public class SpringJoinExecutor implements JoinExecutor {
	
	class Provider{
		private Object bean;
		private Method method;
		private boolean onlyOneParam = false;
		
		public Provider(Object bean, Method method, boolean onlyOneParam) {
			this.bean = bean;
			this.method = method;
			this.onlyOneParam = onlyOneParam;
		}

		public Object invoke(String[] names, Object[] paramValues)throws Exception {
			if(onlyOneParam) {
				Class<?> paramClass = method.getParameters()[0].getType();
				Object param = paramClass.newInstance();
				for(int i = 0; i < names.length; i++) {
					String name = names[i];
					Object value = paramValues[i];
					PropertyUtils.setProperty(param, name, value);
				}
				return method.invoke(bean, param);
			}
			return method.invoke(bean, paramValues);
		}
	}
	
	private Map<String, Provider> providers = new HashMap<>();
	
	private ApplicationContext context;
	
	public SpringJoinExecutor(ApplicationContext context)throws Exception {
		this.context = context;
	}

	@Override
	public Object call(String request, String[] paramNames, Object[] paramValues, Type resultType) throws Exception {
		Provider provider = providers.get(request);
		if(provider == null) {
			throw new ExecuteException("can not find request:" + request);
		}
		Object result = provider.invoke(paramNames, paramValues);
		if(result == null) {
			return null;
		}
		validateResultType(resultType, result);
		return result;
	}

	private void validateResultType(Type resultType, Object result) throws ExecuteException {
		Class<?> cls = result.getClass();
		if(resultType instanceof Class<?>) {
			Class<?> resultClass = (Class<?>)resultType;
			if(!resultClass.isInstance(result)) {
				throw new ExecuteException("result type(" + cls.getName() + ") is not compatible:" + resultClass.getName());
			}
		}
		if(resultType instanceof ParameterizedType) {
			ParameterizedType pt = (ParameterizedType)resultType;
			Class<?> rawType = (Class<?>)pt.getRawType();
			if(!rawType.isInstance(result)) {
				throw new ExecuteException("result type(" + cls.getName() + ") is not compatible:" + rawType.getName());
			}
			Type typ = pt.getActualTypeArguments()[0];
			if(typ instanceof Class<?>) {
				throw new ExecuteException("the member of result`s type(" + typ.getTypeName() + ") is not a Class");
			}
			Class<?> objClass = (Class<?>)typ;
			for(Object obj : (List<?>)result) {
				if(obj != null && !objClass.isInstance(obj)) {
					throw new ExecuteException("the member of result`s type(" + obj.getClass().getName() + ") is not compatible:" + objClass.getName());
				}
			}
		}
	}

	@Override
	public void bind(String request, String[] paramNames, Class<?>[] paramClasses)throws Exception {
		int pos = request.indexOf('.');
		if(pos <= 0) {
			throw new BindException("can not find request[" + request + "]");
		}
		String beanName = request.substring(0, pos);
		String methodName = request.substring(pos + 1);
		Object bean;
		try{
			bean = context.getBean(beanName);
		}catch(Exception e) {
			throw new BindException("can not find bean[" + beanName + "] in request [" + request + "]");
		}
		Class<?> beanClass = AopUtils.getTargetClass(bean);
		Method method;
		try{
			method = beanClass.getMethod(methodName, paramClasses);
		}catch(NoSuchMethodException e) {
			method = null;
		}
		if(method != null) {
			providers.put(request, new Provider(bean, method, false));
			return;
		}
		method = findOneParamMethod(beanClass, methodName);
		if(method == null) {
			throw new BindException("can not find method[" + methodName + "] in Class [" + beanClass.getName() + "]");
		}
		Class<?> paramValueClass = method.getParameters()[0].getType();
		if(paramValueClass.isPrimitive() || paramValueClass.isArray()) {
			throw new BindException("method[" + methodName + "]`s paramClass [" + paramValueClass.getName() + "] is not Object");
		}
		for(int i = 0; i < paramNames.length; i++) {
			String paramName = paramNames[i];
			Field fld = getMatchField(methodName, paramValueClass, paramName);
			if(!fld.getType().isAssignableFrom(paramClasses[i])) {
				throw new BindException("paramClass [" + paramValueClass.getName() + "] of method[" + methodName + "]`s field[" + paramName + "] is not compatible type");
			}
		}
		providers.put(request, new Provider(bean, method, true));
	}

	private Method findOneParamMethod(Class<?> beanClass, String methodName)throws Exception {
		Method result = null;
		for(Method method : beanClass.getMethods()) {
			if(method.getName().equals(methodName) && method.getParameterCount() == 1) {
				if(result != null) {
					throw new BindException("find more method named[" + methodName + "] in Class [" + beanClass.getName() + "]");
				}
				result = method;
			}
		}
		return result;
	}
	
	private Field getMatchField(String methodName, Class<?> cls, String fielName)throws Exception {
		String className = cls.getName();
		Field fld;
		while(true) {
			try{
				fld = cls.getDeclaredField(fielName);
			}catch(NoSuchFieldException e) {
				fld = null;
			}
			if(fld != null) {
				return fld;
			}
			cls = cls.getSuperclass();
			if(cls == Object.class) {
				break;
			}
		}
		throw new BindException("can not find field[" + fielName + "] in method[" + methodName + "]`s paramClass [" + className + "]");
	}

}
