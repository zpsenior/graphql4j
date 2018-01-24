package graphql4j.type;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import graphql4j.operation.GraphQLParser;
import graphql4j.operation.GraphQLReader;
import graphql4j.exception.BindException;
import graphql4j.exception.IntrospectionException;

public class GraphQLSchemaBuilder {

	public GraphQLSchemaBuilder() throws Exception {
		schema = new GraphQLSchema();
	}

	public GraphQLSchemaBuilder(GraphQLSchema schema) throws Exception {
		this.schema = schema;
	}

	private GraphQLSchema schema;
	
	public GraphQLSchema build(String sdl)throws Exception{
		GraphQLReader reader = new GraphQLReader(new GraphQLParser(sdl));
		Type type;
		while(true){
			if(reader.checkName("union")){
				type = buildUnionType(reader);
				schema.addType(type);
				continue;
			}
			if(reader.lookDirective("bind")){
				String bindName = readBind(reader);
				if(reader.checkName("type")){
					type = buildObjectType(reader, bindName);
				}else if(reader.checkName("interface")){
					type = buildInterfaceType(reader, bindName);
				}else if(reader.checkName("input")){
					type = buildInputType(reader, bindName);
				}else if(reader.checkName("enum")){
					type = buildEnumType(reader, bindName);
				}else{
					//throw new IntrospectionException("unexpect.token");
					break;
				}
				schema.addType(type);
			}
		}
		schema.introspection();
		return schema;
	}
	
	private Type buildUnionType(GraphQLReader reader)throws Exception{
		String name = reader.readName();
		reader.readPunctuator("=");
		String type = reader.readName();
		Set<String> types = new HashSet<String>();
		types.add(type);
		while(reader.checkPunctuator("|")){
			type = reader.readName();
			types.add(type);
		}
		return new UnionType(name, types);
	}
	
	private String readBind(GraphQLReader reader)throws Exception{
		reader.readDirective("bind");
		reader.readPunctuator("(");
		String name = reader.readStr();
		reader.readPunctuator(")");
		return name;
	}
	
	private Type buildEnumType(GraphQLReader reader, String className)throws Exception{
		String name = reader.readName();
		String field;
		Set<String> fields = new HashSet<String>();
		reader.readPunctuator("{");
		while(reader.lookName()){
			field = reader.readName();
			if(fields.contains(field)){
				throw new IntrospectionException("duplicate.field.name", field);
			}
			fields.add(field);
		}
		reader.readPunctuator("}");
		return new EnumType(name, className, fields);
	}
	
	private Type buildInputType(GraphQLReader reader, String className)throws Exception{
		String name = reader.readName();
		Class<?> cls = Class.forName(className);
		reader.readPunctuator("{");
		InputObjectField field;
		Set<InputObjectField> fields = new HashSet<InputObjectField>();
		while((field = buildInputObjectField(reader, cls)) != null){
			if(fields.contains(field)){
				throw new IntrospectionException("duplicate.argument.field.name", field.getName());
			}
			fields.add(field);
		}
		reader.readPunctuator("}");
		return new InputObjectType(name, className, fields);
	}
	
	private Type buildInterfaceType(GraphQLReader reader, String className)throws Exception{
		String name = reader.readName();
		Class<?> cls = Class.forName(className);
		reader.readPunctuator("{");
		ObjectField field;
		Set<ObjectField> fields = new HashSet<ObjectField>();
		while((field = buildField(reader, cls)) != null){
			if(fields.contains(field)){
				throw new IntrospectionException("duplicate.field.name", field.getName());
			}
			fields.add(field);
		}
		reader.readPunctuator("}");
		return new InterfaceType(name, className, fields);
	}
	
	private Type buildObjectType(GraphQLReader reader, String className)throws Exception{
		String name = reader.readName();
		Set<String> impls = null;
		if(reader.checkName("implements")){
			String inf = reader.readName();
			if(impls == null){
				impls = new HashSet<String>();
			}
			if(impls.contains(inf)){
				throw new IntrospectionException("duplicate.interface.name", inf);
			}
			impls.add(inf);
		}
		Class<?> cls = Class.forName(className);
		reader.readPunctuator("{");
		ObjectField field;
		Set<ObjectField> fields = new HashSet<ObjectField>();
		while((field = buildField(reader, cls)) != null){
			if(fields.contains(field)){
				throw new IntrospectionException("duplicate.field.name", field.getName());
			}
			fields.add(field);
		}
		reader.readPunctuator("}");
		return new ObjectType(name, impls, className, fields);
	}

	private InputObjectField buildInputObjectField(GraphQLReader reader, Class<?> cls) throws Exception{
		String name = reader.readName();
		String methodMame = "set" + name.substring(0, 1).toUpperCase() + name.substring(1);
		Method method = findMethod(cls, methodMame);
		boolean notNull = false;
		reader.readPunctuator(":");
		Type type = buildFieldType(reader, method.getParameterTypes()[0], method.getGenericParameterTypes()[0]);
		if(reader.checkPunctuator("!")){
			notNull = true;
		}
		return new InputObjectField(name, method, type, notNull, null);
	}

	private Argument buildArgument(GraphQLReader reader, Class<?> cls, java.lang.reflect.Type gtype) throws Exception{
		String name = reader.readName();
		boolean notNull = false;
		reader.readPunctuator(":");
		Type type = buildFieldType(reader, cls, gtype);
		if(reader.checkPunctuator("!")){
			notNull = true;
		}
		return new Argument(name, type, notNull, null);
	}
	
	private String getMethodName(String bindName, String fieldName, boolean noArguments){
		String methodMame;
		if(noArguments){
			methodMame = bindName != null ? bindName : "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
		}else{
			methodMame = bindName != null ? bindName : fieldName;
		}
		return methodMame;
	}

	private ObjectField buildField(GraphQLReader reader, Class<?> cls)throws Exception {
		String bindName = null;
		if(reader.lookDirective("bind")){
			bindName = readBind(reader);
		}
		String name = reader.readName();
		Method method = null;
		String methodMame;
		List<Argument> arguments = null;
		if(reader.checkPunctuator("(")){
			methodMame = getMethodName(bindName, name, false);
			method = findMethod(cls, methodMame);
			Class<?>[] paramTypes = method.getParameterTypes();
			java.lang.reflect.Type[] gtypes = method.getGenericParameterTypes();
			arguments = new ArrayList<Argument>();
			for(int i = 0; i < paramTypes.length; i++){
				Class<?> paramType = paramTypes[i];
				java.lang.reflect.Type gtype = gtypes[i];
				Argument arg = buildArgument(reader, paramType, gtype);
				if(arg == null){
					throw new BindException("no.match.method.parameter.count", name, methodMame);
				}
				arguments.add(arg);
			}
			if(arguments.size() != paramTypes.length){
				throw new BindException("no.match.method.parameter.count", name, methodMame);
			}
			reader.readPunctuator(")");
		}else{
			methodMame = getMethodName(bindName, name, true);
			method = cls.getDeclaredMethod(methodMame);
		}
		reader.readPunctuator(":");
		Type type = buildFieldType(reader, method.getReturnType(), method.getGenericReturnType());
		boolean notNull = false;
		if(reader.checkPunctuator("!")){
			notNull = true;
		}
		return new ObjectField(name, type, method, arguments, notNull);
	}

	private Method findMethod(Class<?> cls, String name)throws Exception {
		Method method = null;
		for(Method m :cls.getDeclaredMethods()){
			if(m.getName().equals(name)){
				method = m;
				break;
			}
		}
		if(method == null){
			throw new BindException("no.find.method.in.class", name, cls.getName());
		}
		return method;
	}

	private Type buildFieldType(GraphQLReader reader, Class<?> cls, java.lang.reflect.Type gtype)throws Exception{
		if(!reader.lookPunctuator("[")){
			String typeName = reader.readName();
			return parseType(typeName);
		}
		return buildFieldArrayType(reader, cls, gtype.toString());
	}
	
	private ArrayType buildFieldArrayType(GraphQLReader reader, Class<?> cls, String gtype)throws Exception{
		Type type;
		reader.readPunctuator("[");
		String className = cls.getName();
		if(!ArrayType.isArrayWrapperClass(cls)){
			throw new BindException("unsurpport.class.as.list.type", className);
		}
		String prefix = className + "<";
		if(!gtype.startsWith(prefix)){
			throw new BindException("error.generic.type", gtype);
		}
		String baseClass = gtype.substring(prefix.length(), gtype.length() - 1);
		if(reader.lookPunctuator("[")){
			int pos = baseClass.indexOf('<');
			if(pos > 0){
				String clsName = baseClass.substring(0, pos);
				type = buildFieldArrayType(reader, Class.forName(clsName), baseClass);
			}else{
				throw new BindException("no.match.generic.type.define", baseClass);
			}
		}else{
			type = parseType(reader.readName());
		}
		boolean childNotNull = false;
		if(reader.checkPunctuator("!")){
			childNotNull = true;
		}
		reader.readPunctuator("]");
		return new ArrayType(type, className, childNotNull);
	}

	private Type parseType(String name){
		Type type = ScalarType.parseScalarType(name);
		if(type != null){
			return type;
		}
		type= schema.getType(name);
		if(type != null){
			return type;
		}
		return new DummyType(name);
	}
}
