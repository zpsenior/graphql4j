package com.zpsenior.graphql4j.utils;

import java.beans.PropertyDescriptor;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.beanutils.PropertyUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zpsenior.graphql4j.ParamFinder;
import com.zpsenior.graphql4j.exception.ConversionException;
import com.zpsenior.graphql4j.input.ArrayType;
import com.zpsenior.graphql4j.input.InputType;

public class JsonParamFinder extends ParamFinder<JsonNode> {
	
	private JsonNode root;
	
	public JsonParamFinder() {}
	
	public void load(String json) throws Exception{
		ObjectMapper mapper = new ObjectMapper();
		root = mapper.readTree(json);
	}

	@Override
	protected JsonNode getObject(String name) throws Exception {
		JsonNode node = root.get(name);
		if(node == null) {
			return null;
		}
		return node;
	}

	@Override
	protected Object convert2Array(Object value, ArrayType at) throws Exception {
		JsonNode node = (JsonNode)value;
		if(!node.isArray()) {
			throw new ConversionException("source type is not array!");
		}
		InputType baseType = at.getBaseType();
		
		Object[] values = new Object[node.size()];
		for(int i = 0 ; i < node.size(); i++) {
			JsonNode item = node.get(i);
			values[i] = convert(item, baseType);
		}
		return values;
	}

	@Override
	protected Object convert2Scalar(Object value, Class<?> bindClass) throws Exception {
		JsonNode node = (JsonNode)value;
		if(!node.isValueNode()) {
			throw new ConversionException("source type is not scalar type!");
		}
		String str = node.asText();
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

	@Override
	protected Object convertObject(Object value, Class<?> bindClass) throws Exception {
		JsonNode node = (JsonNode)value;
		Object target = bindClass.newInstance();
		PropertyDescriptor[] props = PropertyUtils.getPropertyDescriptors(bindClass);
		for(PropertyDescriptor prop : props) {
			String name = prop.getName();
			JsonNode pv = node.get(name);
			Class<?> cls = prop.getPropertyType();
			PropertyUtils.setProperty(target, name, convertObject(pv, cls));
		}
		return target;
	}

}
