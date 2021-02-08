package com.zpsenior.graphql4j.schema;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import com.zpsenior.graphql4j.annotation.Field;
import com.zpsenior.graphql4j.annotation.Join;
import com.zpsenior.graphql4j.exception.TypeException;

public class TypeConfig {
	
	private String name;
	private Class<?> typeClass;
	private Map<String, Member> members = null;

	public TypeConfig(String name, Class<?> typeClass)throws Exception {
		this.name = name;
		this.typeClass = typeClass;
		if(typeClass.isEnum()) {
			return;
		}
		scanField(typeClass);
		scanMethod(typeClass);
		scanJoin(typeClass);
	}

	public String getName() {
		return name;
	}

	public Class<?> getBindClass() {
		return typeClass;
	}

	public Member[] getMembers() {
		if(members == null) {
			return new Member[0];
		}
		return members.values().toArray(new Member[members.size()]);
	}
	
	private void scanMethod(Class<?> cls) throws Exception{
		for(Method method : cls.getMethods()){
			Field field = method.getAnnotation(Field.class);
			if(field == null) {
				continue;
			}
			String[] names = field.value();
			int mod = method.getModifiers();
			if(!Modifier.isPublic(mod)) {
				throw new TypeException("method(" + name + ") must be public");
			}
			if(Modifier.isStatic(mod)) {
				throw new TypeException("method(" + name + ") can not be static");
			}
			if(method.getReturnType() == Void.class) {
				throw new TypeException("method(" + name + ")`s returnType is void");
			}
			if(names == null || names.length <= 0) {
				names = new String[] {method.getName()};
			}
			for(String name : names) {
				addMember(method, name);
			}
		}
	}
	
	private void scanField(Class<?> cls)throws Exception {
		for(java.lang.reflect.Field field : cls.getFields()){
			Field fld = field.getAnnotation(Field.class);
			if(fld == null) {
				continue;
			}
			String[] names = fld.value();	
			int mod = field.getModifiers();
			if(Modifier.isFinal(mod)) {
				throw new TypeException("field(" + name + ") can not be final");
			}
			if(Modifier.isStatic(mod)) {
				throw new TypeException("field(" + name + ") can not be static");
			}
			if(names == null || names.length <= 0) {
				names = new String[] {field.getName()};
			}
			for(String name : names) {
				addMember(field, name);
			}
		}
	}
	private void scanJoin(Class<?> cls)throws Exception {
		for(java.lang.reflect.Field field : cls.getFields()) {
			Join join = field.getAnnotation(Join.class);
			if(join == null) {
				continue;
			}
			String[] names = join.value();	
			int mod = field.getModifiers();
			if(Modifier.isFinal(mod)) {
				throw new TypeException("field(" + name + ") can not be final");
			}
			if(Modifier.isStatic(mod)) {
				throw new TypeException("field(" + name + ") can not be static");
			}

			if(names == null || names.length <= 0) {
				names = new String[] {field.getName()};
			}
			for(String name : names) {
				addMember(field, name);
			}
		}
	}

	private void addMember(AccessibleObject access, String name) throws Exception{
		if(members == null) {
			members = new HashMap<>();
		}
		if(members.containsKey(name)) {
			throw new TypeException("duplicate name:" + name);
		}
		Member member = new Member(access);
		members.put(name, member);
	}

	public Member getMemberByName(String name) {
		return members.get(name);
	}
}
