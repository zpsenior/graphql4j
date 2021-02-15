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
			throw new ConversionException("param(" + name + ")`s type is null");
		}
		return convert(getObject(name), name, type);
	}
	
	protected Object convert(T obj, String name, InputType type)throws Exception {
		if(obj == null) {
			if(type instanceof NotNullType) {
				throw new ConversionException("param(" + name + ") can not be null");
			}
			return null;
		}
		if(type instanceof NotNullType) {
			NotNullType nnt = (NotNullType)type;
			return convert(obj, name, nnt);
		}else if(type instanceof ScalarType) {
			ScalarType st = (ScalarType)type;
			return convert2Scalar(obj, st.getBindClass());
		}else if(type instanceof ArrayType) {
			ArrayType at = (ArrayType)type;
			return convert2Array(obj, name, at);
		}else if(type instanceof NameType) {
			NameType nt = (NameType)type;
			return convert2Object(obj, nt.getBindClass());
		}
		throw new ConversionException("invalid type:" + type.getClass().getName());
	}
	
	protected abstract T getObject(String name) throws Exception;

	protected abstract Object convert2Array(T value, String name, ArrayType at) throws Exception;

	protected abstract Object convert2Scalar(T value, Class<?> cls) throws Exception;

	protected abstract Object convert2Object(T value, Class<?> cls)throws Exception;

}
