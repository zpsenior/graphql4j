package com.zpsenior.graphql4j;

import java.lang.reflect.Type;

public interface JoinExecutor {
	
	Object call(String method, Object[] paramValues, Type resultType)throws Exception;
	
}
