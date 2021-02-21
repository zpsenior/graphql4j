package com.zpsenior.graphql4j.utils;

import java.util.List;

public class SQLBuilder {

	
	public interface Filter{
		
		default String mapTableName(String name, String className) {
			return name;
		}
		
		default boolean filterType(String name) {
			return false;
		}
		
		default boolean filterField(String sqlType, String name){
			return false;
		}
		
		default String getInsertFieldValue(String name){
			return null;
		}
		
		default String getUpdateFieldValue(String name){
			return null;
		}

		default String convertType(String valueType, int len){
			return valueType;
		}
		
	}
	
	public static String toUnderscore(String name) {
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < name.length(); i++) {
			char c = name.charAt(i);
			if(c >= 'A' && c <= 'Z') {
				if(i != 0) {
					sb.append("_");
				}
				c = Character.toLowerCase(c); 
			}
			sb.append(c);
		}
		return sb.toString();
	}
	
	public static String wrapperArray(List<String> keys) {
		StringBuffer sb = new StringBuffer();
		for(String key : keys) {
			if(sb.length() > 0) {
				sb.append(", ");
			}
			sb.append("`");
			sb.append(key);
			sb.append("`");
		}
		return sb.toString();
	}
}
