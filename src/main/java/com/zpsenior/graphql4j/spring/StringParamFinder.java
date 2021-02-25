package com.zpsenior.graphql4j.spring;

import java.util.Map;

import com.zpsenior.graphql4j.ParamFinder;
import com.zpsenior.graphql4j.exception.ConversionException;
import com.zpsenior.graphql4j.input.ArrayType;
import com.zpsenior.graphql4j.input.InputType;
import com.zpsenior.graphql4j.utils.ScalarUtils;

public class StringParamFinder extends ParamFinder<String> {
	
	public StringParamFinder(Map<String, String> params) {
		for(String key : params.keySet()) {
			addParam(key, params.get(key));
		}
	}

	@Override
	protected Object convert2Array(String value, String name, ArrayType at) throws Exception {
		String[] arrays = value.split(",");
		Object[] values = new Object[arrays.length];
		InputType baseType = at.getBaseType();
		for(int i = 0; i < values.length; i++) {
			values[i] = convert(arrays[i], name, baseType);
		}
		return values;
	}

	@Override
	protected Object convert2Scalar(String value, Class<?> bindClass) throws Exception {
		return ScalarUtils.toScalar(bindClass, value);
	}

	@Override
	protected Object convert2Object(String value, Class<?> cls) throws Exception {
		throw new ConversionException("source type can not convert to type" + cls.getName());
	}


}
