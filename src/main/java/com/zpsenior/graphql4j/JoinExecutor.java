package com.zpsenior.graphql4j;

public interface JoinExecutor {
	
	Object call(String method, Object[] paramValues, Class<?> resultType)throws Exception;
	
}
