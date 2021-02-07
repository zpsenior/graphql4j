package com.zpsenior.graphql4j.schema;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.zpsenior.graphql4j.annotation.Field;
import com.zpsenior.graphql4j.annotation.Join;

public class TypeConfig {
	
	private String name;
	private Class<?> typeClass;
	private Map<String, Member> members = null;

	public TypeConfig(String name, Class<?> typeClass) {
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
	
	private void scanMethod(Class<?> cls) {
		Arrays.stream(cls.getMethods())
		.forEach((method)->{
			Field field = method.getAnnotation(Field.class);
			if(field == null) {
				return;
			}
			String[] names = field.value();
			int mod = method.getModifiers();
			if(!Modifier.isPublic(mod)) {
				throw new RuntimeException("method(" + name + ") must be public");
			}
			if(Modifier.isStatic(mod)) {
				throw new RuntimeException("method(" + name + ") can not be static");
			}
			if(method.getReturnType() == Void.class) {
				throw new RuntimeException("method(" + name + ")`s returnType is void");
			}
			if(names == null || names.length <= 0) {
				names = new String[] {method.getName()};
			}
			for(String name : names) {
				addMember(method, name);
			}
		});
	}
	
	private void scanField(Class<?> cls) {
		Arrays.stream(cls.getFields())
		.forEach((field)->{
			Field fld = field.getAnnotation(Field.class);
			if(fld == null) {
				return;
			}
			String[] names = fld.value();	
			int mod = field.getModifiers();
			if(Modifier.isFinal(mod)) {
				throw new RuntimeException("field(" + name + ") can not be final");
			}
			if(Modifier.isStatic(mod)) {
				throw new RuntimeException("field(" + name + ") can not be static");
			}
			if(names == null || names.length <= 0) {
				names = new String[] {field.getName()};
			}
			for(String name : names) {
				addMember(field, name);
			}
		});
	}
	private void scanJoin(Class<?> cls) {
		Arrays.stream(cls.getFields())
		.forEach((field)->{
			Join join = field.getAnnotation(Join.class);
			if(join == null) {
				return;
			}
			String[] names = join.value();	
			int mod = field.getModifiers();
			if(Modifier.isFinal(mod)) {
				throw new RuntimeException("field(" + name + ") can not be final");
			}
			if(Modifier.isStatic(mod)) {
				throw new RuntimeException("field(" + name + ") can not be static");
			}

			if(names == null || names.length <= 0) {
				names = new String[] {field.getName()};
			}
			for(String name : names) {
				addMember(field, name);
			}
		});
	}

	private void addMember(AccessibleObject access, String name) {
		if(members == null) {
			members = new HashMap<>();
		}
		if(members.containsKey(name)) {
			throw new RuntimeException("duplicate name:" + name);
		}
		Member member = new Member(access);
		members.put(name, member);
	}

	public Member getMemberByName(String name) {
		return members.get(name);
	}
}
