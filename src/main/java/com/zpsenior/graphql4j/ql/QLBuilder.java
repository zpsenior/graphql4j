package com.zpsenior.graphql4j.ql;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import com.zpsenior.graphql4j.input.InputType;
import com.zpsenior.graphql4j.input.NotNullType;
import com.zpsenior.graphql4j.input.ScalarType;
import com.zpsenior.graphql4j.value.ArrayValue;
import com.zpsenior.graphql4j.value.ConstValue;
import com.zpsenior.graphql4j.value.ObjectValue;
import com.zpsenior.graphql4j.value.Value;
import com.zpsenior.graphql4j.value.VariableValue;

public class QLBuilder {

	public void build(QLReader reader, QLRoot root)throws Exception{

		Entry entry;
		while(true) {
			if(reader.checkName("query")) {
				entry = buildEntry(reader, EntryKind.Query);
			}else if(reader.checkName("mutation")) {
				entry = buildEntry(reader, EntryKind.Mutation);
			}else if(reader.checkName("subscription")) {
				entry = buildEntry(reader, EntryKind.SUBSCRIPTION);
			}else {
				throw new RuntimeException("unexpect token :" + reader.lookahead(-1));
			}
			root.add(entry);
		}
	}

	private Entry buildEntry(QLReader reader, EntryKind kind)throws Exception {
		String name = reader.readName();
		Set<EntryArgument> arguments = new HashSet<>();
		if(reader.checkPunctuator("(")){
			buildArguments(reader, arguments);
		}
		Set<Element> elements = new LinkedHashSet<>();
		reader.readPunctuator("{");
		buildElements(reader, elements);
		
		return new Entry(name, kind, arguments, elements);
	}
	
	private void buildArguments(QLReader reader, Set<EntryArgument> arguments)throws Exception{
		while(true){
			if(reader.lookVar()){
				EntryArgument arg = buildArgument(reader);
				arguments.add(arg);
				continue;
			}
			break;
		}
		reader.readPunctuator(")");
	}
	
	private EntryArgument buildArgument(QLReader reader)throws Exception{
		Value defaultValue = null;
		String name = reader.readVar();
		reader.readPunctuator(":");
		InputType type = buildInputType(reader);
		if(reader.checkPunctuator("!")){
			type = new NotNullType(type);
		}
		if(reader.checkPunctuator("=")){
			defaultValue = buildParamValue(reader);
		}
		return new EntryArgument(name, type, defaultValue);
	}

	private InputType buildInputType(QLReader reader) {
		String name = reader.readName();
		InputType it = ScalarType.getType(name);
		if(it == null) {
			
		}
		return it;
	}

	private Value buildParamValue(QLReader reader) {
		// TODO Auto-generated method stub
		return null;
	}

	private void buildElements(QLReader reader, Set<Element> elements) {
		while(true){
			if(reader.lookName()){
				Element ele = buildElement(reader);
				elements.add(ele);
				continue;
			}
			break;
		}
		reader.readPunctuator("}");
	}

	private Element buildElement(QLReader reader) {
		String alias = null;
		String name = reader.readName();
		Set<ElementArgument> params = new HashSet<ElementArgument>();
		Set<Element> children = new HashSet<Element>();
		if(reader.checkPunctuator(":")){
			alias = name;
			name = reader.readName();
		}
		if(reader.checkPunctuator("(")){
			buildParams(reader, params);
		}
		if(reader.checkPunctuator("{")){
			buildElements(reader, children);
		}
		return new Element(name, alias, params, children);
	}

	private void buildParams(QLReader reader, Set<ElementArgument> params) {
		while(true){
			if(reader.lookName()){
				ElementArgument param = buildParam(reader);
				params.add(param);
				continue;
			}
			break;
		}
		reader.readPunctuator(")");
	}

	private ElementArgument buildParam(QLReader reader) {
		Value value;
		String name = reader.readName();
		reader.readPunctuator(":");
		if(reader.checkPunctuator("[")){
			value = buildArrayValue(reader);
			reader.readPunctuator("]");
		}else if(reader.checkPunctuator("{")){
			value = buildObjectValue(reader);
			reader.readPunctuator("}");
		}else if(reader.checkPunctuator("$")){
			value = buildVariableValue(reader);
		}else{
			value = buildConstValue(reader);
		}
		return new ElementArgument(name, value);
	}

	private ConstValue buildConstValue(QLReader reader) {
		// TODO Auto-generated method stub
		return null;
	}

	private VariableValue buildVariableValue(QLReader reader) {
		// TODO Auto-generated method stub
		return null;
	}

	private ObjectValue buildObjectValue(QLReader reader) {
		// TODO Auto-generated method stub
		return null;
	}

	private ArrayValue buildArrayValue(QLReader reader) {
		// TODO Auto-generated method stub
		return null;
	}
}
