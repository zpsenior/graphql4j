package com.zpsenior.graphql4j.ql;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zpsenior.graphql4j.JoinExecutor;
import com.zpsenior.graphql4j.exception.BindException;
import com.zpsenior.graphql4j.schema.Member;
import com.zpsenior.graphql4j.schema.Schema;
import com.zpsenior.graphql4j.schema.TypeConfig;

public class Element extends QLNode implements Comparable<Element>{
	
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
		Object value = null;
		value = member.invoke(context, obj);
		if(value == null || member.isScalarType()) {
			return value;
		}
		if(member.isListType()) {
			List<Object> list = new ArrayList<>();
			for(Object o : (List<?>)value) {
				Object item = buildChildrenValue(context, o);
				list.add(item);
			}
			return list;
		}
		if(children.length > 0) {
			return buildChildrenValue(context, value);
		}
		return value;
	}
	
	private Object buildChildrenValue(QLContext context, Object inst) throws Exception {
		Map<String, Object> values = new HashMap<>();
		for(Element child : children) {
			Object value = child.execute(context, inst);
			values.put(child.getAlias(), value);
		}
		return values;
	}

	public void bind(Schema schema, JoinExecutor joinExecutor, TypeConfig parent)throws Exception {
		Member member = parent.getMemberByName(name);
		if(member == null) {
			throw new BindException("can not find [" + name + "] in " + parent.getName());
		}
		if(member.isMethod()) {
			member.bindArgumentValues(arguments);
		}
		if(member.getJoin() != null){
			Class<?> parentClass = parent.getBindClass();
			member.bindJoin(joinExecutor, parentClass);
		}
		this.member = member;
		if(children != null && children.length > 0) {
			if(member.isScalarType()) {
				throw new BindException("scalar type can not be split!");
			}
			Class<?> valueType = member.getValueType();
			if(member.isListType()) {
				ParameterizedType pt = (ParameterizedType)member.getValueGenericType();
				valueType = (Class<?>)pt.getActualTypeArguments()[0];
			}
			TypeConfig typeConfig = schema.getTypeConfig(valueType);
			for(Element child : children) {
				child.bind(schema, joinExecutor, typeConfig);
			}
		}
	}

	@Override
	public int compareTo(Element target) {
		return name.compareTo(target.name);
	}

	public void toString(int deep, StringBuffer sb) {
		String prefix = String.format("%" + deep + "s", ""); 
		sb.append(prefix);
		if(!name.equals(alias)) {
			sb.append(alias).append(":");
		}
		sb.append(name);
		if(arguments.length > 0) {
			sb.append("(");
			boolean first = true;
			for(ElementArgument arg : arguments){
				if(first) {
					first = false;
				}else {
					sb.append(", ");
				}
				arg.toString(0, sb);
			}
			sb.append(")");
		}
		if(children != null && children.length > 0) {
			sb.append("{").append("\n");
			for(Element child : children) {
				child.toString(deep + 3, sb);
				sb.append("\n");
			}
			sb.append(prefix).append("}");
		}
	}
	
}
