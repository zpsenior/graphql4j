package com.zpsenior.graphql4j.spring;

import java.beans.PropertyDescriptor;
import java.io.InputStream;

import org.apache.commons.beanutils.PropertyUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zpsenior.graphql4j.ParamFinder;
import com.zpsenior.graphql4j.exception.ConversionException;
import com.zpsenior.graphql4j.input.ArrayType;
import com.zpsenior.graphql4j.input.InputType;
import com.zpsenior.graphql4j.utils.ScalarUtils;

public class JsonParamFinder extends ParamFinder<JsonNode> {
	
	private JsonNode root;
	
	public JsonParamFinder(InputStream input) throws Exception{
		this((new ObjectMapper()).readTree(input));
	}
	
	public JsonParamFinder(String json) throws Exception{
		this((new ObjectMapper()).readTree(json));
	}
	
	public JsonParamFinder(JsonNode root) {
		this.root = root;
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
	protected Object convert2Array(JsonNode node, String name, ArrayType at) throws Exception {
		if(!node.isArray()) {
			throw new ConversionException("source type is not array!");
		}
		InputType baseType = at.getBaseType();
		
		Object[] values = new Object[node.size()];
		for(int i = 0 ; i < node.size(); i++) {
			JsonNode item = node.get(i);
			values[i] = convert(item, name, baseType);
		}
		return values;
	}

	@Override
	protected Object convert2Scalar(JsonNode node, Class<?> bindClass) throws Exception {
		if(!node.isValueNode()) {
			throw new ConversionException("source type is not scalar type!");
		}
		String str = node.asText();
		return ScalarUtils.toScalar(bindClass, str);
	}

	@Override
	protected Object convert2Object(JsonNode node, Class<?> bindClass) throws Exception {
		Object target = bindClass.newInstance();
		PropertyDescriptor[] props = PropertyUtils.getPropertyDescriptors(bindClass);
		for(PropertyDescriptor prop : props) {
			String name = prop.getName();
			JsonNode pv = node.get(name);
			Class<?> cls = prop.getPropertyType();
			Object paramObject;
			if(ScalarUtils.isScalarType(cls)) {
				if(!pv.isValueNode()) {
					throw new ConversionException("param(" + name + ") type is not scalar type!");
				}
				paramObject = ScalarUtils.toScalar(bindClass, pv.asText());
			}else {
				paramObject = convert2Object(pv, cls);
			}
			PropertyUtils.setProperty(target, name, paramObject);
		}
		return target;
	}

}
