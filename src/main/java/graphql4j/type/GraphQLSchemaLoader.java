package graphql4j.type;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import graphql4j.annotation.GraphQLArgument;
import graphql4j.annotation.GraphQLEnum;
import graphql4j.annotation.GraphQLField;
import graphql4j.annotation.GraphQLInput;
import graphql4j.annotation.GraphQLInterface;
import graphql4j.annotation.GraphQLObject;
import graphql4j.annotation.GraphQLUnion;
import graphql4j.exception.BindException;
import graphql4j.type.Argument;
import graphql4j.type.EnumType;
import graphql4j.type.InputObjectType;
import graphql4j.type.ObjectField;
import graphql4j.type.ObjectType;
import graphql4j.type.InterfaceType;
import graphql4j.type.Type;

public class GraphQLSchemaLoader implements TypeFinder {
	
	private static Logger log = LogManager.getLogger(GraphQLSchemaLoader.class);
	
	private GraphQLSchema schema;
	
	public GraphQLSchemaLoader(GraphQLSchema schema){
		this.schema = schema;
	}
	
	public GraphQLSchemaLoader(){
		schema = new GraphQLSchema();
	}
	
	public GraphQLSchema load(Class<?>[] classes)throws Exception{
		for(Class<?> cls : classes){
			GraphQLObject ann = cls.getAnnotation(GraphQLObject.class);
			if(ann == null){
				continue;
			}
			String name = ann.value();
			if("Query".equals(name)||"Mutation".equals(name)){
				buildType(cls);
			}
		}
		bindUnionTypes(schema.getAllTypes());
		schema.introspection();
		return schema;
	}

	private void bindUnionTypes(Collection<Type> binds)throws Exception {
		if(binds.size() <= 0){
			return;
		}
		Map<String, Set<String>> mapping = new HashMap<String, Set<String>>();
		for(Type bind : binds){
			if(bind instanceof UnionType){
				continue;
			}
			Class<?> cls = Class.forName(bind.getBindClass());
			GraphQLUnion gqlUnion = cls.getAnnotation(GraphQLUnion.class);
			if(gqlUnion == null){
				continue;
			}
			String name = gqlUnion.value();
			GraphQLObject gqlObject = cls.getAnnotation(GraphQLObject.class);
			if(gqlObject == null){
				throw new BindException("can.not.bind.union.type.in.class.not.bind.object.type", cls.getName());
			}
			String typeName = gqlObject.value();
			if("".equals(typeName)){
				typeName = cls.getSimpleName();
			}
			Set<String> types = mapping.get(name);
			if(types == null){
				types = new HashSet<String>();
				mapping.put(name, types);
			}
			types.add(typeName);
		}
		for(String name : mapping.keySet()){
			Set<String> types = mapping.get(name);
			if(types.size() <= 1){
				throw new BindException("union.type.must.have.more.than.two.object.type", name);
			}
			Type type = new UnionType(name, types);
			schema.addType(type);
		}
	}

	private Type buildType(Class<?> cls) throws Exception {
		Type type = null;
		Annotation[] annotations = cls.getAnnotations();
		for(Annotation ann : annotations){
			if(ann instanceof GraphQLObject){
				type = buildClassType(cls);
			}else if(ann instanceof GraphQLInterface){
				type = buildInterfaceType(cls);
			}else if(ann instanceof GraphQLInput){
				type = buildInputType(cls);
			}else if(ann instanceof GraphQLEnum){
				type = buildEnumType(cls);
			}else{
				continue;
			}
			schema.addType(type);
		}
		if(type == null){
			throw new BindException("can.not.find.type.mapping.the.class", cls.getName());
		}
		return type;
	}

	private String getSimpleName(Class<?> cls, String value)throws Exception{
		if(value != null && !"".equals(value)){
			return value;
		}
		return cls.getSimpleName();
	}
	
	private Method[] filterMethods(Class<?> cls, String prefix, int paramCount, boolean returnValue)throws Exception{
		List<Method> methods = new ArrayList<Method>();
		for(Method m : cls.getMethods()){
			if(!Modifier.isPublic(m.getModifiers())){
				continue;
			}
			if(checkMethod(m, prefix, paramCount, returnValue)){
				methods.add(m);
			}else if("get".equals(prefix) && m.getName().startsWith("is")) {
				Class<?> returnClass = m.getReturnType();
				Type t = ScalarType.getTypeByClass(returnClass);
				if(t == ScalarType.Boolean) {
					methods.add(m);
				}
			}
		}
		
		return methods.toArray(new Method[methods.size()]);
	}
	
	private boolean checkMethod(Method method, String prefix, int paramCount, boolean returnValue)throws Exception{
		String name = method.getName();
		if(prefix != null){
			if(!name.startsWith(prefix)){
				return false;
			}
		}
		Class<?>[] paramTypes = method.getParameterTypes();
		if(paramCount >= 0 && paramTypes.length != paramCount){
			return false;
		}
		if(paramTypes.length == 0 && "getClass".equals(name)){
			return false;
		}
		Class<?> returnType = method.getReturnType();
		boolean flag = !returnType.getName().equals("void") ^ returnValue;
		if(flag){
			return false;
		}
		return true;
	}
	
	private ObjectType buildClassType(Class<?> cls)throws Exception{
		String name;
		GraphQLObject gqlObject = cls.getAnnotation(GraphQLObject.class);
		if(cls.isEnum()||cls.isInterface()){
				throw new BindException("interface.or.enum.can.not.be.bind.type");
		}
		Set<String> impls = getInterfaceName(cls);
		name = getSimpleName(cls, gqlObject.value());
		//log.debug("className: " + name + " " + gqlObject.valueObject());
		List<ObjectField> fields = buildObjectFields(cls, gqlObject.valueObject());
		ObjectType ot = new ObjectType(name, impls, cls.getName(), fields);
		return ot;
	}

	private Set<String> getInterfaceName(Class<?> cls)throws Exception {
		Set<String> set = new HashSet<String>();
		Set<Class<?>> parents = getClassParents(cls);
		for(Class<?> inf : parents){
			Annotation ann = inf.getAnnotation(GraphQLInterface.class);
			if(ann != null){
				String name = ((GraphQLInterface)ann).value();
				if(name == null || "".equals(name)){
					name = inf.getSimpleName();
				}
				set.add(name);
			}
		}
		if(set.size() <= 0){
			return null;
		}
		return set;
	}
	
	private Set<Class<?>> getClassParents(Class<?> cls)throws Exception{
		Set<Class<?>> set = new HashSet<Class<?>>();
		for(Class<?> c : cls.getInterfaces()){
			set.add(c);
		}
		Class<?> parent = cls;
		while(true){
			parent = parent.getSuperclass();
			if("java.lang.Object".equals(parent.getName())){
				break;
			}
			set.add(parent);
		}
		return set;
	}

	private InterfaceType buildInterfaceType(Class<?> cls)throws Exception{
		String name;
		GraphQLInterface gqlInterface = cls.getAnnotation(GraphQLInterface.class);
		if(!cls.isInterface()){
			throw new BindException("just.interface.can.be.bind");
		}
		name = getSimpleName(cls, gqlInterface.value());
		//log.debug("interfaceName: " + name + " " + gqlInterface.valueObject());
		List<ObjectField> fields = buildObjectFields(cls, gqlInterface.valueObject());
		InterfaceType itype = new InterfaceType(name, cls.getName(), fields);
		return itype;

	}
	
	private InputObjectType buildInputType(Class<?> cls)throws Exception{
		String name;
		GraphQLInput gqlInput = cls.getAnnotation(GraphQLInput.class);
		List<InputObjectField> fields = buildInputObjectFields(cls);
		name = getSimpleName(cls, gqlInput.value());
		try{
			cls.newInstance();
		}catch(Throwable e){
			throw new BindException("input.object.type.must.bind.object.can.be.created.with.no.params");
		}
		return new InputObjectType(name, cls.getName(), fields);
	}
	
	private EnumType buildEnumType(Class<?> cls)throws Exception{
		String name;
		GraphQLEnum gqlEnum = cls.getAnnotation(GraphQLEnum.class);
		if(!cls.isEnum()){
			throw new BindException("just.enum.can.be.bind");
		}
		Object[] objs = cls.getEnumConstants();
		List<String> fields = new ArrayList<String>();
		for (Object obj : objs) {
			fields.add(obj.toString());
		}
		name = getSimpleName(cls, gqlEnum.value());
		return new EnumType(name, cls.getName(), fields);
	}
	
	private String getFieldName(String name, String prefix, String defaultValue)throws Exception{
		if(defaultValue != null && !"".equals(defaultValue)){
			return defaultValue;
		}
		if(name.startsWith(prefix)){
			int len = prefix.length();
			name = name.substring(len);
			name = name.substring(0, 1).toLowerCase() + name.substring(1);
			return name;
		}
		if("get".equals(prefix) && name.startsWith("is")) {
			name = name.substring(2);
			name = name.substring(0, 1).toLowerCase() + name.substring(1);
			return name;
		}
		throw new BindException("method.name.must.prefix.by", prefix);
	}

	private List<InputObjectField> buildInputObjectFields(Class<?> cls)throws Exception{
		Method[] methods = filterMethods(cls, "set", 1, false);
		if(methods.length <= 0){
			throw new BindException("input.type.must.have.field");
		}
		List<InputObjectField> fields = new ArrayList<InputObjectField>();
		for(Method method : methods){
			GraphQLField gqlField = method.getAnnotation(GraphQLField.class);
			String bindName = null;
			String defaultValue = null;
			boolean notNull = false;
			if(gqlField != null){
				bindName = gqlField.value();
				defaultValue = gqlField.defaultValue();
				notNull = gqlField.notNull();
			}
			Class<?> paramClass = method.getParameterTypes()[0];
			java.lang.reflect.Type paramType = method.getGenericParameterTypes()[0];
			Type type = getTypeByClass(null, paramClass, paramType, true);
			String name = method.getName();
			name = getFieldName(name, "set", bindName);
			InputObjectField arg = new InputObjectField(name, method, type, notNull, defaultValue);
			fields.add(arg);
		}
		return fields;
	}

	private List<ObjectField> buildObjectFields(Class<?> cls, boolean valueObject)throws Exception{
		Method[] methods;
		if(valueObject){
			methods = filterMethods(cls, "get", 0, true);
		}else{
			methods = filterMethods(cls, null, -1, true);
		}
		List<ObjectField> fields = new ArrayList<ObjectField>();
		for(Method method : methods){
			GraphQLField gphQLField = method.getAnnotation(GraphQLField.class);
			if(!valueObject && gphQLField == null){
				continue;
			}
			String bindName = null;
			boolean notNull = false;
			if(gphQLField != null){
				bindName = gphQLField.value();
				notNull = gphQLField.notNull();
			}
			String name = method.getName();
			log.debug(cls.getName() + " method: " + name);
			List<Argument> arguments = getArguments(method);
			if(arguments == null){
				name = getFieldName(name, "get", bindName);
			}else{
				if(bindName != null && !"".equals(bindName)){
					name = bindName;
				}
			}
			Type type;
			Class<?> returnClass = method.getReturnType();
			GraphQLUnion gqlUnion = returnClass.getAnnotation(GraphQLUnion.class);
			if(gqlUnion != null){
				if(!returnClass.getName().equals("java.lang.Object")){
					throw new BindException("union.type.must.bind.method.return.object");
				}
				type = new DummyType(gqlUnion.value());
			}else{
				java.lang.reflect.Type gtype = method.getGenericReturnType();
				type = getTypeByClass(cls, returnClass, gtype, false);
			}
			ObjectField field = new ObjectField(name, type, method, arguments, notNull);
			fields.add(field);
		}
		return fields;
	}
	
	private Type getTypeByClass(Class<?> parentClass, Class<?> returnClass, java.lang.reflect.Type returnType, boolean input)throws Exception{
		log.debug("class: " + returnClass.getName());
		if(ArrayType.isArrayWrapperClass(returnClass)){
			return ArrayType.buildNestedArrayType(parentClass, returnClass, returnType, this, input);
		}
		if(parentClass != null && parentClass.getName().equals(returnClass.getName())) {
			return new DummyType(parentClass.getName());
		}
		return getTypeByClass(returnClass, input);
	}

	private List<Argument> getArguments(Method method)throws Exception{
		List<Argument> args = new ArrayList<Argument>();
		Annotation[][] paramAnnotations = method.getParameterAnnotations();
		Class<?>[] paramTypes = method.getParameterTypes();
		for(int i = 0; i < paramTypes.length; i++){
			Annotation[] annotations = paramAnnotations[i];
			Class<?> c = paramTypes[i];
			Argument arg = getArgument(annotations, c);
			if(arg == null){
				throw new BindException("method.parameter.must.bind.annotation");
			}
			args.add(arg);
		}
		if(args.size() <= 0){
			return null;
		}
		return args;
	}

	private Argument getArgument(Annotation[] annotations, Class<?> c)throws Exception{
		for(Annotation ann : annotations){
			if(ann instanceof GraphQLArgument){
				GraphQLArgument ga = (GraphQLArgument)ann;
				String name = ga.value();
				boolean notNull = ga.notNull();
				String defaultValue = ga.defaultValue();
				Type type = getTypeByClass(c, true);
				return new Argument(name, type, notNull, defaultValue);
			}
		}
		return null;
	}

	public Type getTypeByClass(Class<?> cls, boolean isInput)throws Exception{
		Type type = schema.getTypeByClass(cls, isInput);
		if(type == null){
			type = buildType(cls);
		}
		return type;
	}

}
