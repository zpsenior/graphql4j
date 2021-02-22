package com.zpsenior.graphql4j;

import java.lang.reflect.Type;

public interface JoinExecutor {
	
	Object call(String request, String[] paramNames, Object[] paramValues, Type resultType)throws Exception;

	void bind(String request, String[] paramNames, Class<?>[] paramClasses)throws Exception;
	
}
