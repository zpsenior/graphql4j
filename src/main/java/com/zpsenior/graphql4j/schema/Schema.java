package com.zpsenior.graphql4j.schema;

import java.util.List;
import java.lang.reflect.ParameterizedType;
import java.util.LinkedHashMap;
import java.util.Map;

import com.zpsenior.graphql4j.annotation.Type;
import com.zpsenior.graphql4j.exception.TypeException;
import com.zpsenior.graphql4j.utils.ScalarUtils;

public class Schema {
	
	private Map<String, TypeConfig> configs = new LinkedHashMap<>();
	
	public Schema(Class<?> query, Class<?> mutation)throws Exception {
		
		init(query, mutation);
	}

	private void init(Class<?> clsQuery, Class<?> clsMutation)throws Exception {
		TypeConfig config = buildTypeConfig(clsQuery);
		if(config == null || !"Query".equals(config.getName())) {
			throw new TypeException(clsQuery.getName() + " is not Query!");
		}
		config = buildTypeConfig(clsMutation);
		if(config == null || !"Mutation".equals(config.getName())) {
			throw new TypeException(clsMutation.getName() + " is not Mutation!");
		}
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for(String key : configs.keySet()) {
			TypeConfig config = configs.get(key);
			config.toString(sb);
			sb.append("\n");
		}
		return sb.toString();
	}

	private TypeConfig buildTypeConfig(Class<?> cls)throws Exception {
		logout("buildTypeConfig ->" + cls.getName());
		String typeName = getTypeName(cls);
		if(configs.containsKey(typeName)) {
			return null;
		}
		TypeConfig config = new TypeConfig(typeName, cls);
		configs.put(typeName, config);
		for(Member member : config.getMembers()){
			buildReturnType(member);
		}
		return config;
	}

	private void buildReturnType(Member member) throws Exception {
		java.lang.reflect.Type valueType = member.getValueGenericType();
		logout("buildReturnType ->" + valueType.getTypeName() + " " + member.getName());
		Class<?> valueClass;
		if(valueType instanceof ParameterizedType) {
			ParameterizedType pt = (ParameterizedType)valueType;
			Class<?> orgClass = (Class<?>)pt.getRawType();
			if(orgClass != List.class) {
				throw new TypeException("parameterized type must be List.class");
			}
			valueClass = (Class<?>)pt.getActualTypeArguments()[0];
		}else {
			valueClass = (Class<?>)valueType;
		}
		
		while(valueClass.isArray()) {
			valueClass = valueClass.getComponentType();
		}
		logout("returnType ->" + valueClass.getName());
		if(ScalarUtils.isScalarType(valueClass)) {
			return;
		}
		buildTypeConfig(valueClass);
	}
	
	public String[] getTypeNames() {
		return configs.keySet().toArray(new String[configs.size()]);
	}
	
	public TypeConfig getTypeConfig(String name) {
		return configs.get(name);
	}

	public TypeConfig getTypeConfig(Class<?> cls) throws Exception{
		String typeName = getTypeName(cls);
		TypeConfig config = getTypeConfig(typeName);
		if(config == null) {
			throw new TypeException("can not find type:" + typeName);
		}
		return config;
	}

	private String getTypeName(Class<?> cls) throws Exception{
		Type type = cls.getAnnotation(Type.class);
		if(type == null) {
			throw new TypeException("[" + cls.getName() + "] is not annotated by Type!");
		}
		String typeName = type.value();
		if(typeName == null || "".equals(typeName)) {
			typeName = cls.getSimpleName();
		}
		return typeName;
	}
	
	private void logout(String msg) {
		//System.out.println(msg);
	}
}
