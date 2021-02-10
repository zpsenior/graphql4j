package com.zpsenior.graphql4j.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ScalarUtils {

	
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

	public static Object toScalar(Class<?> bindClass, String str) {
		if(bindClass == Byte.class) {
			return Byte.parseByte(str);
		}else if(bindClass == Character.class) {
			return (byte)Integer.parseInt(str);
		}else if(bindClass == Short.class) {
			return Short.parseShort(str);
		}else if(bindClass == Integer.class) {
			return Integer.parseInt(str);
		}else if(bindClass == Float.class) {
			return Float.parseFloat(str);
		}else if(bindClass == Double.class) {
			return Double.parseDouble(str);
		}else if(bindClass == Boolean.class) {
			return Boolean.parseBoolean(str);
		}else if(bindClass == Date.class) {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			return df.format(str);
		}else if(bindClass == BigInteger.class) {
			return new BigInteger(str);
		}else if(bindClass == BigDecimal.class) {
			return new BigDecimal(str);
		}//String.class
		return str;
	}
}
