package com.zpsenior.graphql4j.schema;

import java.util.List;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.zpsenior.graphql4j.Utils;
import com.zpsenior.graphql4j.annotation.Type;

public class Schema {
	
	private Object query;
	private Object mutation;
	
	private Map<String, TypeConfig> configs = new HashMap<>();
	
	public Schema(Object query, Object mutation) {
		this.query = query;
		this.mutation = mutation;
		
		init(query.getClass(), mutation.getClass());
	}

	private void init(Class<?> clsQuery, Class<?> clsMutation) {
		TypeConfig config = buildTypeConfig(clsQuery);
		if(config == null || !"Query".equals(config.getName())) {
			throw new RuntimeException(clsQuery.getName() + " is not Query!");
		}
		config = buildTypeConfig(clsMutation);
		if(config == null || !"Mutation".equals(config.getName())) {
			throw new RuntimeException(clsMutation.getName() + " is not Mutation!");
		}
	}
	
	

	public Object getQuery() {
		return query;
	}

	public Object getMutation() {
		return mutation;
	}

	private TypeConfig buildTypeConfig(Class<?> cls) {
		String typeName = getTypeName(cls);
		if(configs.containsKey(typeName)) {
			return null;
		}
		TypeConfig config = new TypeConfig(typeName, cls);
		configs.put(typeName, config);
		Arrays.stream(config.getMembers())
		.forEach((member)->{
			Class<?> valueType;
			if(member.isMethod()) {
				Method method = (Method)member.getAccess();
				valueType = method.getReturnType();
			}else {
				Field field = (Field)member.getAccess();
				valueType = field.getType();
			}
			while(valueType.isArray()) {
				valueType = valueType.getComponentType();
			}
			if(valueType == List.class) {
				valueType = valueType.getTypeParameters()[0].getClass();
			}
			if(Utils.isScalarType(valueType)) {
				return;
			}
			buildTypeConfig(valueType);
		});
		return config;
	}
	
	public TypeConfig getTypeConfig(String name) {
		return configs.get(name);
	}

	public TypeConfig getTypeConfig(Class<?> cls) {
		String typeName = getTypeName(cls);
		TypeConfig config = getTypeConfig(typeName);
		if(config == null) {
			throw new RuntimeException("can not find type:" + typeName);
		}
		return config;
	}

	private String getTypeName(Class<?> cls) {
		Type type = cls.getAnnotation(Type.class);
		if(type == null) {
			throw new RuntimeException(cls.getName() + " is not annotated by Type!");
		}
		String typeName = type.value();
		if(typeName == null || "".equals(typeName)) {
			typeName = cls.getSimpleName();
		}
		return typeName;
	}
}
