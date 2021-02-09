package com.zpsenior.graphql4j.ql;

import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.zpsenior.graphql4j.exception.CompileException;
import com.zpsenior.graphql4j.input.ArrayType;
import com.zpsenior.graphql4j.input.InputFinder;
import com.zpsenior.graphql4j.input.InputType;
import com.zpsenior.graphql4j.input.NameType;
import com.zpsenior.graphql4j.input.NotNullType;
import com.zpsenior.graphql4j.input.ScalarType;
import com.zpsenior.graphql4j.parser.Token;
import com.zpsenior.graphql4j.value.ArrayValue;
import com.zpsenior.graphql4j.value.ConstValue;
import com.zpsenior.graphql4j.value.ObjectValue;
import com.zpsenior.graphql4j.value.Value;
import com.zpsenior.graphql4j.value.VariableValue;


public class QLBuilder {
	
	private InputFinder finder;

	public void build(Reader rd, InputFinder finder, QLRoot root)throws Exception{
		QLReader reader = new QLReader(rd);
		this.finder = finder;
		Entry entry;
		while(true) {
			if(reader.checkKeyword("query")) {
				entry = buildEntry(reader, EntryKind.Query);
			}else if(reader.checkKeyword("mutation")) {
				entry = buildEntry(reader, EntryKind.Mutation);
			//}else if(reader.checkKeyword("subscription")) {
			//	entry = buildEntry(reader, EntryKind.SUBSCRIPTION);
			}else {
				throw new CompileException("expect 'query', 'mutation', but token :" + reader.lookahead(0));
			}
			root.add(entry);
			if(reader.eof()) {
				break;
			}
		}
	}
	
	private Set<String> vars = new HashSet<>();

	private Entry buildEntry(QLReader reader, EntryKind kind)throws Exception {
		vars.clear();
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
			EntryArgument arg = buildArgument(reader);
			arguments.add(arg);
			if(reader.checkPunctuator(",")) {
				continue;
			}
			break;
		}
		reader.readPunctuator(")");
	}
	
	private EntryArgument buildArgument(QLReader reader)throws Exception{
		Value defaultValue = null;
		reader.readPunctuator("$");
		String name = reader.readName();
		reader.readPunctuator(":");
		InputType type = buildInputType(reader);
		if(reader.checkPunctuator("!")){
			type = new NotNullType(type);
		}
		if(reader.checkPunctuator("=")){
			defaultValue = buildDefaultValue(reader);
		}
		if(vars.contains(name)) {
			throw new CompileException("duplication variable:" + name);
		}
		vars.add(name);
		return new EntryArgument(name, type, defaultValue);
	}

	private InputType buildInputType(QLReader reader) throws Exception{
		InputType it;
		if(reader.checkPunctuator("[")){
			it = buildArrayType(reader);
		}else{
			String name = reader.readName();
			ScalarType st = ScalarType.getType(name);
			if(st != null) {
				return st;
			}
			Class<?> cls = finder.findClass(name);
			it = new NameType(name, cls);
		}
		return it;
	}
	
	private ArrayType buildArrayType(QLReader reader)throws Exception{
		InputType type = buildInputType(reader);
		if(reader.checkPunctuator("!")){
			type = new NotNullType(type);
		}
		reader.readPunctuator("]");
		return new ArrayType(type);
	}

	private void buildElements(QLReader reader, Set<Element> elements) throws Exception{
		while(true){
			Element ele = buildElement(reader);
			elements.add(ele);
			if(reader.checkPunctuator(",")){
				continue;
			}
			break;
		}
		reader.readPunctuator("}");
	}

	private Element buildElement(QLReader reader)throws Exception {
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

	private void buildParams(QLReader reader, Set<ElementArgument> params)throws Exception {
		while(true){
			String name = reader.readName();
			reader.readPunctuator(":");
			Value value = buildValue(reader);
			ElementArgument param = new ElementArgument(name, value);
			params.add(param);
			if(reader.checkPunctuator(",")){
				continue;
			}
			break;
		}
		reader.readPunctuator(")");
	}

	private Value buildDefaultValue(QLReader reader)throws Exception {
		Value value;
		if(reader.checkPunctuator("[")){
			value = buildArrayValue(reader, true);
			reader.readPunctuator("]");
		}else if(reader.checkPunctuator("{")){
			value = buildObjectValue(reader, true);
			reader.readPunctuator("}");
		}else {
			value = buildConstValue(reader);
		}
		return value;
	}

	private Value buildValue(QLReader reader) throws Exception{
		Value value;
		if(reader.checkPunctuator("[")){
			value = buildArrayValue(reader, false);
			reader.readPunctuator("]");
		}else if(reader.checkPunctuator("{")){
			value = buildObjectValue(reader, false);
			reader.readPunctuator("}");
		}else if(reader.checkPunctuator("$")){
			value = buildVariableValue(reader);
		}else {
			value = buildConstValue(reader);
		}
		return value;
	}

	private ConstValue buildConstValue(QLReader reader) throws Exception{
		Token t = reader.readToken();
		return new ConstValue(t.getContent());
	}

	private VariableValue buildVariableValue(QLReader reader) throws Exception{
		String varName = reader.readName();
		if(!vars.contains(varName)) {
			throw new CompileException("not define variable:"+ varName);
		}
		return new VariableValue(varName);
	}

	private ObjectValue buildObjectValue(QLReader reader, boolean notVariable)throws Exception {
		Value value;
		Map<String, Value> values = new HashMap<>();
		while(true){
			String varName = reader.readName();
			reader.readPunctuator(":");
			value = notVariable? buildDefaultValue(reader) : buildValue(reader);
			values.put(varName, value);
			if(reader.checkPunctuator(",")) {
				continue;
			}
			break;
		}
		return new ObjectValue(values);
	}

	private ArrayValue buildArrayValue(QLReader reader, boolean notVariable) throws Exception{
		Value value;
		List<Value> values = new ArrayList<Value>();
		while(true){
			value = notVariable? buildDefaultValue(reader) : buildValue(reader);
			if(value == null) {
				break;
			}
			values.add(value);
		}
		return new ArrayValue(values);
	}
}
