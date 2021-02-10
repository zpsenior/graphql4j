package com.zpsenior.graphql4j.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;

import org.springframework.aop.support.AopUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.zpsenior.graphql4j.JoinExecutor;
import com.zpsenior.graphql4j.exception.ExecuteException;

public class SpringJoinExecutor implements JoinExecutor {
	
	class Provider{
		private Object bean;
		private Method method;
		
		public Provider(Object bean, Method method) {
			this.bean = bean;
			this.method = method;
		}

		public Object invoke(Object[] paramValues)throws Exception {
			return method.invoke(bean, paramValues);
		}
	}
	
	private Map<String, Provider> providers;
	
	public SpringJoinExecutor(ApplicationContext context)throws Exception {
		this(context, JoinMap.class);
	}

	public SpringJoinExecutor(ApplicationContext context, Class<? extends Annotation> annotationType)throws Exception {
		Map<String, Object> beans = context.getBeansWithAnnotation(Component.class);
		boolean isJoinMap = annotationType == JoinMap.class;
		for (String name : beans.keySet()) {
			Object bean = beans.get(name);
			Class<?> beanClass = AopUtils.getTargetClass(bean);
			for(Method method : beanClass.getDeclaredMethods()) {
				int mod = method.getModifiers();
				Annotation ann = method.getAnnotation(annotationType);
				if(ann != null && Modifier.isPublic(mod) && !Modifier.isStatic(mod)) {
					String methodName = method.getName();
					Class<?>[] paramTypes = method.getParameterTypes();
					Method beanMethod = bean.getClass().getMethod(methodName, paramTypes);
					Provider provider = new Provider(bean, beanMethod);
					String key = isJoinMap ? ((JoinMap)ann).value() : name + "." + methodName;
					if(providers.containsKey(key)) {
						throw new ExecuteException("duplicate key : " + key);
					}
					providers.put(key, provider);
				}
			}
		}
	}

	@Override
	public Object call(String request, Object[] paramValues, Class<?> resultType) throws Exception {
		Provider provider = providers.get(request);
		if(provider == null) {
			throw new ExecuteException("can not find request:" + request);
		}
		Object result = provider.invoke(paramValues);
		if(result != null && !resultType.isInstance(result)) {
			throw new ExecuteException("result type(" + result.getClass().getName() + ") is not compatible:" + resultType.getName());
		}
		return result;
	}

}
