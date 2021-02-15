package com.zpsenior.graphql4j.spring;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.zpsenior.graphql4j.ParamFinder;
import com.zpsenior.graphql4j.exception.ConversionException;
import com.zpsenior.graphql4j.input.ArrayType;
import com.zpsenior.graphql4j.utils.ScalarUtils;

public class StringParamFinder extends ParamFinder<String> {
	
	private Map<String, String> paramValues = new HashMap<>();
	
	public StringParamFinder(Map<String, String[]> params) {
		for(String key : params.keySet()) {
			String value;
			String[] pv = params.get(key);
			if(pv.length == 1) {
				value = pv[0];
			}else {
				value = Arrays.toString(pv);
			}
			paramValues.put(key, value);
		}
	}
	
	public Map<String, String> getParamValues(){
		return paramValues;
	}

	@Override
	protected String getObject(String name) throws Exception {
		return paramValues.get(name);
	}

	@Override
	protected Object convert2Array(String value, String name, ArrayType at) throws Exception {
		return value.split(",");
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
