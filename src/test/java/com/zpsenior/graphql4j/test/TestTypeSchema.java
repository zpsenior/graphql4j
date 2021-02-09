package com.zpsenior.graphql4j.test;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.zpsenior.graphql4j.schema.Schema;
import com.zpsenior.graphql4j.test.vo.Mutation;
import com.zpsenior.graphql4j.test.vo.Query;


public class TestTypeSchema {
	
	private Date dt = new Date();
	
	private List<Map<String, Object>> fields = new ArrayList<>();

	public static void main(String[] args) {
		try {
			loop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void loop()throws Exception {
		/*
		Class<?> cls = TestTypeSchema.class;
		
		String[] names = new String[] {"dt", "fields"};
		
		for(String name : names) {
			
			Field fld = cls.getDeclaredField(name);
			
			Type tp = fld.getGenericType();
			
			System.out.println("TypeName:" + tp.getTypeName());
			
			if(tp instanceof ParameterizedType) {
				ParameterizedType pt = (ParameterizedType)tp;
				System.out.println("rawType:" + pt.getRawType().getTypeName());
				System.out.println("rawType1:" + ((Class<?>)pt.getRawType()).getName());
				for(Type typ : pt.getActualTypeArguments()) {
					System.out.println("ParameterizedTypeName:" + typ.getTypeName() + " " + typ.getClass());
					ParameterizedType t = (ParameterizedType)typ;
					System.out.println("param type:" + t.getRawType());
				}
			}else {
				Class<?> cl = (Class<?>)tp;
				System.out.println("ClassName:" + cl.getName());
			}
		}*/
		
		
		try {
			Schema schema = new Schema(new Query(), new Mutation());
			schema.printSchema();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
