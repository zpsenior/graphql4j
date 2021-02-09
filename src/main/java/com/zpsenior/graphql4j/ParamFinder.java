package com.zpsenior.graphql4j;

import com.zpsenior.graphql4j.exception.ConversionException;
import com.zpsenior.graphql4j.input.ArrayType;
import com.zpsenior.graphql4j.input.InputType;
import com.zpsenior.graphql4j.input.NameType;
import com.zpsenior.graphql4j.input.NotNullType;
import com.zpsenior.graphql4j.input.ScalarType;

public abstract class ParamFinder<T> {

	public Object getParam(String name, InputType type)throws Exception {
		if(type == null) {
			throw new ConversionException("type is null");
		}
		Object obj = getObject(name);
		if(obj == null) {
			return null;
		}
		return convert(obj, type);
	}
	
	protected Object convert(Object obj, InputType type)throws Exception {
		if(type instanceof NotNullType) {
			NotNullType nnt = (NotNullType)type;
			return convert(obj, nnt);
		}else if(type instanceof ScalarType) {
			ScalarType st = (ScalarType)type;
			return convert2Scalar(obj, st.getBindClass());
		}else if(type instanceof ArrayType) {
			ArrayType at = (ArrayType)type;
			return convert2Array(obj, at);
		}else if(type instanceof NameType) {
			NameType nt = (NameType)type;
			return convertObject(obj, nt.getBindClass());
		}
		throw new ConversionException("invalid type:" + type.getClass().getName());
	}
	
	protected abstract T getObject(String name) throws Exception;

	protected abstract Object convert2Array(Object value, ArrayType at) throws Exception;

	protected abstract Object convert2Scalar(Object value, Class<?> cls) throws Exception;

	protected abstract Object convertObject(Object value, Class<?> cls)throws Exception;

}
