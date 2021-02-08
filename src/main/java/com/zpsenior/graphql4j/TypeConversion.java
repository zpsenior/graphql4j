package com.zpsenior.graphql4j;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zpsenior.graphql4j.exception.ConversionException;
import com.zpsenior.graphql4j.input.ArrayType;
import com.zpsenior.graphql4j.input.InputType;
import com.zpsenior.graphql4j.input.NameType;
import com.zpsenior.graphql4j.input.NotNullType;
import com.zpsenior.graphql4j.input.ScalarType;

public class TypeConversion {
	
	public static Object conversion(InputType type, Object source)throws Exception {
		if(type == null) {
			throw new ConversionException("type is null");
		}
		if(source == null) {
			if(type instanceof NotNullType) {
				throw new ConversionException("value must be not null");
			}
			return null;
		}
		if(type instanceof ScalarType) {
			ScalarType st = (ScalarType)type;
			return parseValue(st, source);
		}
		if(type instanceof NotNullType) {
			return conversion(((NotNullType)type).getBaseType(), source);
		}
		if(type instanceof ArrayType) {
			InputType baseType = ((ArrayType)type).getBaseType();
			Object[] array;
			if(source instanceof Collection) {
				array = ((Collection<?>)source).toArray();
			}else if(source.getClass().isArray()) {
				array = (Object[])source;
			}else {
				throw new ConversionException("invalid type(" + source.getClass().getName() + ") conversion to array");
			}
			List<Object> list = new ArrayList<>();
			for(Object obj : array) {
				list.add(conversion(baseType, obj));
			}
		}
		if(type instanceof NameType) {
			Class<?> bindClass = ((NameType)type).getBindClass();
			return conversion(bindClass, source);
		}
		throw new ConversionException("invalid type:" + type.getClass().getName());
	}

	public static Object conversion(Class<?> targetClass, Object source)throws Exception {
		if(source == null) {
			return null;
		}
		Class<?> sourceClass = source.getClass();
		if(targetClass.isInstance(source)) {
			return source;
		}
		ScalarType st = ScalarType.getType(targetClass);
		if(st != null) {
			return parseValue(st, source);
		}
		Object target = targetClass.newInstance();
		Map<String, Field> fields = new HashMap<>();
		Arrays.stream(targetClass.getFields())
			.forEach((field)->{
					int mod = field.getModifiers();
					if(Modifier.isStatic(mod)|| Modifier.isFinal(mod)) {
						return;
					}
					String name = field.getName();
					fields.put(name, field);
				});
		for(String name : fields.keySet()) {
			Field fldTarget = fields.get(name);
			if(!existField(source, sourceClass, name)) {
				continue;
			};
			Object value = conversion(fldTarget.getType(), getFieldValue(source, sourceClass, name));
			fldTarget.setAccessible(true);
			fldTarget.set(target, value);
		}
		return target;
	}

	@SuppressWarnings("rawtypes")
	private static boolean existField(Object obj, Class<?> cls, String name) {
		if(obj instanceof Map) {
			Map map = (Map)obj;
			if(map.containsKey(name)) {
				return true;
			}
			return false;
		}
		try {
			cls.getField(name);
		}catch(NoSuchFieldException e) {
			return false;
		}
		return true;
	}

	@SuppressWarnings("rawtypes")
	private static Object getFieldValue(Object obj, Class<?> cls, String name) throws Exception {
		if(obj instanceof Map) {
			Map map = (Map)obj;
			if(map.containsKey(name)) {
				return map.get(name);
			}
			throw new ConversionException("can not find field(" + name + ") in object:" + cls.getName());
		}
		Field field = cls.getField(name);
		field.setAccessible(true);
		return field.get(obj);
	}
	

	
	public static Object parseValue(ScalarType st, Object value) throws Exception {
		if(value == null){
			return null;
		}
		if(st == ScalarType.Boolean){
			return parseBoolean(value);
		}else if(st == ScalarType.Int){
			return parseInt(value);
		}else if(st == ScalarType.Long){
			return parseLong(value);
		}else if(st == ScalarType.Float){
			return parseFloat(value);
		}else if(st == ScalarType.Double){
			return parseDouble(value);
		}else if(st == ScalarType.String){
			return value.toString();
		}else if(st == ScalarType.Short){
			return parseShort(value);
		}else if(st == ScalarType.Char){
			return parseChar(value);
		}else if(st == ScalarType.Byte){
			return parseByte(value);
		}else if(st == ScalarType.Date){
			return parseDate(value);
		}else if(st == ScalarType.BigInt){
			return parseBigInt(value);
		}else if(st == ScalarType.BigDecimal){
			return parseBigDecimal(value);
		}
		throw new ConversionException("error.type");
	}
	

	private static Object parseDouble(Object value)throws Exception {
		if(value instanceof java.lang.Double){
			return value;
		}
		if(value instanceof java.lang.Number){
			return ((Number)value).doubleValue();
		}
		if(value instanceof java.lang.String){
			return java.lang.Double.parseDouble((java.lang.String)value);
		}
		throw new ConversionException("error.double.type", value);
	}


	private static Object parseFloat(Object value)throws Exception {
		if(value instanceof java.lang.Float){
			return value;
		}
		if(value instanceof java.lang.Integer || value instanceof java.lang.Short || value instanceof java.lang.Byte){
			return ((Number)value).floatValue();
		}
		if(value instanceof java.lang.String){
			return java.lang.Float.parseFloat((java.lang.String)value);
		}
		throw new ConversionException("error.float.type", value);
	}


	private static Object parseLong(Object value)throws Exception {
		if(value instanceof java.lang.Long){
			return value;
		}
		if(value instanceof java.lang.Integer || value instanceof java.lang.Short || value instanceof java.lang.Byte){
			return ((Number)value).longValue();
		}
		if(value instanceof java.lang.String){
			return java.lang.Long.parseLong((java.lang.String)value);
		}
		throw new ConversionException("error.long.type", value);
	}


	private static Object parseInt(Object value)throws Exception{
		if(value instanceof java.lang.Integer){
			return value;
		}
		if(value instanceof java.lang.Short || value instanceof java.lang.Byte){
			return ((Number)value).intValue();
		}
		if(value instanceof java.lang.String){
			return java.lang.Integer.parseInt((java.lang.String)value);
		}
		throw new ConversionException("error.int.type", value);
	}


	private static Object parseShort(Object value)throws Exception{
		if(value instanceof java.lang.Short){
			return value;
		}
		if(value instanceof java.lang.Byte){
			return ((Number)value).shortValue();
		}
		if(value instanceof java.lang.String){
			return java.lang.Short.parseShort((java.lang.String)value);
		}
		throw new ConversionException("error.short.type", value);
	}

	private static Object parseChar(Object value)throws Exception{
		if(value instanceof java.lang.Character){
			return value;
		}
		if(value instanceof java.lang.Number){
			return (char)((Number)value).intValue();
		}
		if(value instanceof java.lang.String){
			return (char)(Integer.parseInt((String)value));
		}
		throw new ConversionException("error.short.type", value);
	}

	private static Object parseByte(Object value)throws Exception{
		if(value instanceof java.lang.Byte){
			return value;
		}
		if(value instanceof java.lang.String){
			return java.lang.Byte.parseByte((java.lang.String)value);
		}
		throw new ConversionException("error.byte.type", value);
	}


	private static Object parseBoolean(Object value)throws Exception{
		if(value instanceof java.lang.Boolean){
			return value;
		}
		if(value instanceof java.lang.String){
			return java.lang.Boolean.parseBoolean((java.lang.String)value);
		}
		throw new ConversionException("error.boolean.type", value);
	}

	private static Object parseDate(Object value)throws Exception{
		if(value instanceof java.util.Date){
			return value;
		}
		if(value instanceof java.lang.Long){
			return new java.util.Date((java.lang.Long)value);
		}
		if(value instanceof java.lang.String){
			return new java.util.Date(java.lang.Long.parseLong((java.lang.String)value));
		}
		throw new ConversionException("error.date.type", value);
	}

	private static Object parseBigInt(Object value)throws Exception{
		if(value instanceof java.math.BigInteger){
			return value;
		}
		if(value instanceof java.lang.String){
			return new java.math.BigInteger((java.lang.String)value);
		}
		throw new ConversionException("error.big.int.type", value);
	}

	private static Object parseBigDecimal(Object value)throws Exception{
		if(value instanceof java.math.BigDecimal){
			return value;
		}
		if(value instanceof java.lang.String){
			return new java.math.BigDecimal((java.lang.String)value);
		}
		throw new ConversionException("error.big.decimal.type", value);
	}
}
