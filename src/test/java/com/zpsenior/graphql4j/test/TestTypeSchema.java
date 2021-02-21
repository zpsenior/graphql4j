package com.zpsenior.graphql4j.test;

import com.zpsenior.graphql4j.schema.Schema;


public class TestTypeSchema {

	public static void main(String[] args) {
		try {
			doMain(args[0], args[1]);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void doMain(String query, String mutation)throws Exception {
		
		Class<?> queryClass = Class.forName(query);
		Class<?> mutationClass = Class.forName(mutation);
		
		Schema schema = new Schema(queryClass, mutationClass);
		System.out.println(schema.toString());
	}

}
