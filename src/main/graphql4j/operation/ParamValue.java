package graphql4j.operation;

import graphql4j.JObject;

import graphql4j.type.Type;

public abstract class ParamValue extends JObject {
	
	public abstract Object getValue(Type type)throws Exception;
	
}
