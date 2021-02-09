package com.zpsenior.graphql4j;

public interface JoinExecutor {
	
	Object call(String scope, String method, Object[] paramValues)throws Exception;
	
}
