package graphql4j.operation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import graphql4j.Token;
import graphql4j.exception.BindException;
import graphql4j.exception.TokenException;
import graphql4j.type.GraphQLSchema;
import graphql4j.type.ArrayType;
import graphql4j.type.Type;

public class GraphQLBuilder {
	private GraphQLSchema schema;

	public GraphQLBuilder() {
		this(new GraphQLSchema());
	}

	public GraphQLBuilder(GraphQLSchema schema) {
		this.schema = schema;
	}
	
	public void addDefaultArgument(String name, String typeName)throws Exception{
		Type type = schema.getType(typeName);
		if(type == null){
			throw new BindException("can.not.find.type.name", typeName);
		}
		defaultArguments.put(name, type);
	}
	
	private Map<String, QueryArgument> arguments = null;
	
	private Map<String, Type> defaultArguments = new HashMap<String, Type>();
	
	private void addDefaultArgument(Map<String, QueryArgument> arguments)throws Exception{
		for(String name : defaultArguments.keySet()){
			Type type = defaultArguments.get(name); 
			QueryArgument qa = new QueryArgument(name, type, true, null);
			arguments.put(name, qa);
		}
	}
	
	private ArrayType buildArrayType(GraphQLReader reader)throws Exception{
		Type type = buildType(reader);
		boolean childNotNull = false;
		if(reader.checkPunctuator("!")){
			childNotNull = true;
		}
		reader.readPunctuator("]");
		return new ArrayType(type, "", childNotNull);
	}
	
	private QueryArgument buildArgument(GraphQLReader reader)throws Exception{
		ParamValue defaultValue = null;
		boolean notNull = false;
		String name = reader.readVar();
		reader.readPunctuator(":");
		Type kind = buildType(reader);
		if(reader.checkPunctuator("!")){
			notNull = true;
		}
		if(reader.checkPunctuator("=")){
			defaultValue = buildParamValue(reader);
		}
		return new QueryArgument(name, kind, notNull, defaultValue);
	}

	private Type buildType(GraphQLReader reader) throws Exception {
		Type kind;
		if(reader.checkPunctuator("[")){
			kind = buildArrayType(reader);
		}else{
			kind = schema.getInputType(reader.readName());
		}
		return kind;
	}
	
	private void buildArguments(GraphQLReader reader, Map<String, QueryArgument> arguments)throws Exception{
		while(true){
			if(reader.lookVar()){
				QueryArgument arg = buildArgument(reader);
				arguments.put(arg.getName(), arg);
				continue;
			}
			break;
		}
		reader.readPunctuator(")");
	}
	
	public GraphQL build(String query)throws Exception{
		GraphQL gql = new GraphQL();
		GraphQLReader reader = new GraphQLReader(new GraphQLParser(query));
		buildOperation(reader, gql);
		while(reader.checkName("fragment")){
			String name = reader.readName();
			Fragment fr = buildFragment(reader, gql, name);
			gql.addFragments(name, fr);
		}
		gql.introspection(schema);
		return gql;
	}
	
	public void buildOperation(GraphQLReader reader, GraphQL gql)throws Exception{
		int type = GraphQL.TYPE_QUERY;
		String action = "";
		String optName = reader.readName();
		if("query".equals(optName)){
			type = GraphQL.TYPE_QUERY;
		}else if("mutation".equals(optName)){
			type = GraphQL.TYPE_MUTATION;
		}else if("subscription".equals(optName)){
			type = GraphQL.TYPE_SUBSCRIPTION;
		}else{
			throw new TokenException("unexpect.token", reader.lookahead(-1));
		}
		if(reader.lookName()){
			action = reader.readName();
		}
		if(reader.checkPunctuator("(")){
			arguments = new HashMap<String, QueryArgument>();
			addDefaultArgument(arguments);
			buildArguments(reader, arguments);
		}
		Set<Entity> entities = new LinkedHashSet<Entity>();
		reader.readPunctuator("{");
		buildEntities(reader, gql, entities);
		gql.addOperation(type, action, arguments.values(), entities);
	}

	private Fragment buildFragment(GraphQLReader reader, GraphQL gql, String name)throws Exception{
		reader.readName("on");
		String typeName = reader.readName();
		reader.readPunctuator("{");
		Set<Entity> children = new LinkedHashSet<Entity>();
		buildEntities(reader, gql, children);
		Fragment fr = new Fragment(name, typeName, children);
		return fr;
	}

	private void buildEntities(GraphQLReader reader, GraphQL gql, Set<Entity> set)throws Exception{
		while(true){
			if(reader.lookName()){
				Entity entity = buildEntity(reader, gql);
				set.add(entity);
				continue;
			}else if(reader.checkPunctuator("...")){
				String name;
				Fragment fr = null;
				if(reader.lookName("on")){
					name = Fragment.INLINE_PREFIX + gql.getFragmentCount();
					fr = buildFragment(reader, gql, name);
					gql.addFragments(name, fr);
				}else{
					name = reader.readName();
				}
				Entity entity = new Entity(name, fr);
				set.add(entity);
				continue;
			}
			break;
		}
		reader.readPunctuator("}");
	}
	
	private Entity buildEntity(GraphQLReader reader, GraphQL gql)throws Exception{
		String alias = null;
		String name = reader.readName();
		Set<Param> params = new HashSet<Param>();
		Set<Entity> body = new HashSet<Entity>();
		if(reader.checkPunctuator(":")){
			alias = name;
			name = reader.readName();
		}
		if(reader.checkPunctuator("(")){
			buildEntityParams(reader, params);
		}
		if(reader.checkPunctuator("{")){
			buildEntities(reader, gql, body);
		}
		return new Entity(name, alias, params, body);
	}

	private void buildEntityParams(GraphQLReader reader, Set<Param> params)throws Exception{
		while(true){
			if(reader.lookName()){
				Param param = buildEntityParam(reader);
				params.add(param);
				continue;
			}
			break;
		}
		reader.readPunctuator(")");
	}

	private Param buildEntityParam(GraphQLReader reader)throws Exception{
		ParamValue value;
		String name = reader.readName();
		reader.readPunctuator(":");
		if(reader.checkPunctuator("[")){
			value = buildParamArray(reader);
			reader.readPunctuator("]");
		}else if(reader.checkPunctuator("{")){
			value = buildParamObject(reader);
			reader.readPunctuator("}");
		}else{
			value = buildParamValue(reader);
		}
		return new Param(name, value);
	}
	
	private ParamArray buildParamArray(GraphQLReader reader)throws Exception{
		ParamValue value;
		List<ParamValue> values = new ArrayList<ParamValue>();
		while((value = buildParamValue(reader))!=null){
			values.add(value);
		}
		return new ParamArray(values);
	}
	
	private ParamObject buildParamObject(GraphQLReader reader)throws Exception{
		Map<String, ParamValue> map = new HashMap<String, ParamValue>();
		while(reader.lookName()){
			String varName = reader.readName();
			reader.readPunctuator(":");
			ParamValue value = buildParamValue(reader);
			map.put(varName, value);
		}
		return new ParamObject(map);
	}
	
	private ParamValue buildParamValue(GraphQLReader reader)throws Exception{
		ParamValue value;
		if(reader.lookVar()){
			String varName = reader.readVar();
			QueryArgument arg;
			if(arguments == null || !arguments.containsKey(varName)){
				throw new BindException("undefined.variable", varName);
			}
			arg = arguments.get(varName);
			return new ParamVariable(arg);
		}else if(reader.checkPunctuator("[")){
			value = buildParamArray(reader);
			reader.readPunctuator("]");
			return value;
		}else if(reader.checkPunctuator("{")){
			value = buildParamObject(reader);
			reader.readPunctuator("}");
			return value;
		}
		Token t = reader.readToken();
		ParamConst pc = buildParamConst(t);
		if(pc == null){
			reader.forward(-1);
			return null;
		}
		return pc;
	}
	
	private ParamConst buildParamConst(Token t)throws Exception {
		int tokenType = t.getType();
		switch(tokenType){
			case Token.TOKEN_TYPE_NULL: 
			case Token.TOKEN_TYPE_STRING: 
			case Token.TOKEN_TYPE_BOOLEAN:
			case Token.TOKEN_TYPE_NUMBER: 
				break;
			default:
				return null;
		}
		return new ParamConst(t);
	}
}
