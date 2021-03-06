package com.zpsenior.graphql4j.ql;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.zpsenior.graphql4j.JoinExecutor;
import com.zpsenior.graphql4j.exception.BindException;
import com.zpsenior.graphql4j.schema.Schema;
import com.zpsenior.graphql4j.schema.TypeConfig;
import com.zpsenior.graphql4j.value.VariableValue;

public class Entry extends QLNode implements Comparable<Entry>{
	private String name;
	private EntryKind kind;
	private EntryArgument[] arguments;
	private Element[] elements;
	
	public Entry(String name, EntryKind kind, Collection<EntryArgument> arguments, Collection<Element> elements){
		this.name = name;
		this.kind = kind;
		if(arguments != null) {
			this.arguments  = arguments.toArray(new EntryArgument[arguments.size()]);
		}else {
			this.arguments  = new EntryArgument[0];
		}
		if(elements != null) {
			this.elements  = elements.toArray(new Element[elements.size()]);
		}else {
			this.elements  = new Element[0];
		}
	}
	
	public String getName() {
		return name;
	}
	public EntryKind getKind() {
		return kind;
	}
	public EntryArgument[] getArguments() {
		return arguments;
	}

	public Element[] getElements() {
		return elements;
	}
	
	public Object execute(QLContext context, Object obj) throws Exception{
		context.bindVariable(arguments);
		if(elements.length == 1) {
			Element ele = elements[0];
			return ele.execute(context, obj);
		}
		Map<String, Object> map = new HashMap<>();
		for(Element ele : elements) {
			Object value = ele.execute(context, obj);
			map.put(ele.getAlias(), value);
		}
		return map;
	}
	
	public void bind(Schema schema, JoinExecutor joinExecutor) throws Exception{
		String name = kind == EntryKind.Query ? "Query" : "Mutation";
		TypeConfig typeConfig = schema.getTypeConfig(name);
		for(Element ele : elements) {
			ele.bind(schema, joinExecutor, typeConfig);
		}
	}

	@Override
	public int compareTo(Entry target) {
		return name.compareTo(target.name);
	}
	
	public void toString(int deep, StringBuffer sb) {
		sb.append(kind == EntryKind.Query ? "query" : "mutation").append(" ");
		sb.append(name);
		if(arguments.length > 0) {
			sb.append("(");
			boolean first = true;
			for(EntryArgument arg : arguments) {
				if(first) {
					first = false;
				}else {
					sb.append(", ");
				}
				arg.toString(0, sb);
			}
			sb.append(")");
		}
		sb.append(" {\n");
		for(Element ele : elements) {
			ele.toString(3, sb);
			sb.append("\n");
		}
		sb.append("}\n");
	}

	protected void checkVariable(VariableValue val, Class<?> type) throws Exception{
		String name = val.getVarName();
		for(EntryArgument arg : arguments) {
			if(name.equals(arg.getName())) {
				if(!arg.getType().compatible(type)) {
					throw new BindException("type(" + arg.getType() + ") is not compatible" + type.getName());
				}else {
					return;
				}
			}
		}
		throw new BindException("can not find variable : " + name + " in entry :" + this.name);
	}

}
