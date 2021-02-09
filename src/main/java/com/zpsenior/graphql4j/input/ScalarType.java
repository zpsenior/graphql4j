package com.zpsenior.graphql4j.input;

import java.util.HashMap;
import java.util.Map;

public class ScalarType implements InputType {

	public final static String TYPE_BOOLEAN = "Boolean";
	public final static String TYPE_STRING = "String";
	public final static String TYPE_BYTE = "Byte";
	public final static String TYPE_SHORT = "Short";
	public final static String TYPE_INT = "Int";
	public final static String TYPE_FLOAT = "Float";
	public final static String TYPE_DOUBLE = "Double";
	public final static String TYPE_LONG = "Long";
	public final static String TYPE_CHAR = "Char";
	public final static String TYPE_DATE = "Date";
	public final static String TYPE_BIGINT = "BigInteger";
	public final static String TYPE_BIGDECIMAL = "BigDecimal";
	
	@SuppressWarnings("serial")
	private final static Map<String, ScalarType> types = new HashMap<String, ScalarType>() {

			{
				put(TYPE_BOOLEAN, Boolean);
				put(TYPE_STRING, String);
				put(TYPE_BYTE, Byte);
				put(TYPE_SHORT, Short);
				put(TYPE_INT, Int);
				put(TYPE_FLOAT, Float);
				put(TYPE_DOUBLE, Double);
				put(TYPE_LONG, Long);
				put(TYPE_CHAR, Char);
				put(TYPE_DATE, Date);
				put(TYPE_BIGINT, BigInt);
				put(TYPE_BIGDECIMAL, BigDecimal);
			}
	};

	public final static ScalarType Boolean = new ScalarType(TYPE_BOOLEAN);
	public final static ScalarType String  = new ScalarType(TYPE_STRING);
	public final static ScalarType Byte    = new ScalarType(TYPE_BYTE);
	public final static ScalarType Short   = new ScalarType(TYPE_SHORT);
	public final static ScalarType Int     = new ScalarType(TYPE_INT);
	public final static ScalarType Float   = new ScalarType(TYPE_FLOAT);
	public final static ScalarType Long    = new ScalarType(TYPE_LONG);
	public final static ScalarType Double   = new ScalarType(TYPE_DOUBLE);
	public final static ScalarType Char     = new ScalarType(TYPE_CHAR);
	public final static ScalarType Date     = new ScalarType(TYPE_DATE);
	public final static ScalarType BigInt     = new ScalarType(TYPE_BIGINT);
	public final static ScalarType BigDecimal  = new ScalarType(TYPE_BIGDECIMAL);
	
	private String name;


	public String getName() {
		return name;
	}
	
	private ScalarType(String name){
		this.name = name;
	}
	
	
	public static ScalarType getType(String name) {
		for(String type :types.keySet()) {
			if(type.equals(name)) {
				return types.get(name);
			}
		}
		return null;
	}
	
	public static ScalarType getType(Class<?> cls) {
		return getType(cls.getSimpleName());
	}

	public Class<?> getBindClass()throws Exception {
		String className;
		if(TYPE_DATE.equals(name)) {
			className = "java.util." + name;
		}else if(TYPE_BIGDECIMAL.equals(name)){
			className = "java.math." + name;
		}else if(TYPE_BIGINT.equals(name)){
			className = "java.math." + name;
		}else {
			className = "java.lang." + name;
		}
		return Class.forName(className);
	}

}
