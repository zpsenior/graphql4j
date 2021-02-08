package com.zpsenior.graphql4j.ql;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zpsenior.graphql4j.Utils;
import com.zpsenior.graphql4j.exception.BindException;
import com.zpsenior.graphql4j.schema.Member;
import com.zpsenior.graphql4j.schema.Schema;
import com.zpsenior.graphql4j.schema.TypeConfig;

public class Element implements Comparable<Element>{
	
	private String name;
	private String alias;
	
	private ElementArgument[] arguments;
	private Element[] children;
	
	private Member member;
	
	public Element(String name, String alias, Collection<ElementArgument> arguments, Collection<Element> children) {
		this.name = name;
		if(alias != null){
			this.alias = alias;
		}else{
			this.alias = name;
		}
		if(arguments != null) {
			this.arguments  = arguments.toArray(new ElementArgument[arguments.size()]);
		}else {
			this.arguments  = new ElementArgument[0];
		}
		if(children != null) {
			this.children  = children.toArray(new Element[children.size()]);
		}else {
			this.children  = new Element[0];
		}
	}

	public String getName() {
		return name;
	}

	public String getAlias() {
		return alias;
	}

	public Element[] getChildren() {
		return children;
	}

	public ElementArgument[] getArguments() {
		return arguments;
	}

	public Object execute(QLContext context, Object obj)throws Exception {
		Class<?> valueType = member.getValueType();
		Object value = null;
		value = member.invoke(obj, buildMethodParams(context));
		if(value == null || Utils.isScalarType(valueType)) {
			return value;
		}
		if(Utils.isListType(valueType)) {
			List<Object> list = new ArrayList<>();
			for(Object o : (List<?>)value) {
				Object item = buildChildrenValue(context, o);
				list.add(item);
			}
			return list;
		}
		return buildChildrenValue(context, value);
	}
	
	private Object buildChildrenValue(QLContext context, Object inst) throws Exception {
		Map<String, Object> childValues = new HashMap<>();
		Map<String, Object> values = new HashMap<>();
		for(Element child : children) {
			if(child.member.getJoinMethod() == null) {
				Object value = child.execute(context, inst);
				values.put(child.getAlias(), value);
				childValues.put(child.name, value);
			}
		}
		for(Element child : children) {
			Member member = child.member;
			String joinMethod = member.getJoinMethod();
			if(joinMethod != null) {
				Object value = context.call(joinMethod, member.getJoinParams(), childValues);
				values.put(child.getAlias(), value);
			}
		}
		return values;
	}

	private Map<String, Object> buildMethodParams(QLContext context) {
		if(arguments == null) {
			return null;
		}
		Map<String, Object> values = new HashMap<>();
		for(ElementArgument arg : arguments){
			Object value = arg.getValue(context);
			values.put(arg.getName(), value);
		}
		return values;
	}

	public void bind(Schema schema, TypeConfig parent)throws Exception {
		Member member = parent.getMemberByName(name);
		if(member == null) {
			throw new BindException("can not find :" + name + " in " + parent.getName());
		}
		if(member.isMethod()) {
			for(ElementArgument arg : arguments) {
				String name = arg.getName();
				if(!member.containsParam(name)) {
					throw new BindException("can not find param(" + name + ") in " + parent.getName());
				}
			}
		}
		this.member = member;
		if(children != null && children.length > 0) {
			Class<?> valueType = member.getValueType();
			if(Utils.isScalarType(valueType)) {
				throw new BindException("scalar type can not be split!");
			}
			if(Utils.isListType(valueType)) {
				valueType = valueType.getTypeParameters()[0].getClass();
			}
			TypeConfig typeConfig = schema.getTypeConfig(valueType);
			for(Element child : children) {
				child.bind(schema, typeConfig);
			}
		}
	}

	@Override
	public int compareTo(Element target) {
		return name.compareTo(target.name);
	}

}
