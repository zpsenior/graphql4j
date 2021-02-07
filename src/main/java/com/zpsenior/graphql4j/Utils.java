package com.zpsenior.graphql4j;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

public class Utils {

	
	private final static Class<?>[] scalars = new Class[] {
			String.class,
			Boolean.class,
			Byte.class,
			Character.class,
			Short.class,
			Integer.class,
			Float.class,
			Long.class,
			Double.class,
			BigDecimal.class,
			BigInteger.class,
			Date.class,
	};

	public static boolean isScalarType(Class<?> type) {
		if(type.isPrimitive()) {
			return true;
		}
		if(type.isEnum()) {
			return true;
		}
		for(Class<?> scalar : scalars) {
			if(scalar == type) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isListType(Class<?> type) {
		return type.isAssignableFrom(List.class);
	}
}
