package graphql4j.type;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import graphql4j.exception.IntrospectionException;

public class GraphQLSchema implements TypeFinder {
	
	private static Logger log = LogManager.getLogger(GraphQLSchema.class);

	private Map<String, Type> types = new HashMap<String, Type>();
	
	public GraphQLSchema(){
	}
	
	protected Collection<Type> getAllTypes(){
		return types.values();
	}
	
	protected void addType(Type type)throws Exception{
		String name = type.getName();
		if(!types.containsKey(name)){
			types.put(name, type);
			return;
		}
		//log.debug(types);
		log.error("duplicate.type:" + name);
		throw new IntrospectionException("duplicate.type", name);
	}


	public Type getTypeByClass(Class<?> cls, boolean isInput)throws Exception{
		Type type;
		if(cls.isArray()){
			type = getTypeByClass(cls.getComponentType(), isInput);
			if(type != null){
				return new ArrayType(type, "", false);
			}
			return null;
		}
		type = ScalarType.getTypeByClass(cls);
		if(type != null){
			return type;
		}
		String clsName = cls.getName();
		for(String name : types.keySet()){
			Type bind = types.get(name);
			if(bind instanceof UnionType){
				continue;
			}
			Class<?> bindClass = Class.forName(bind.getBindClass());
			if(bindClass.getName().equals(clsName)){
				if(!isInput && (bind instanceof InputObjectType)){
					//throw new IntrospectionException("diff.not.null.setting.of.type", isInput);
					continue;
				}
				if(isInput) {
					log.debug("find input class: " + clsName);
				}else {
					log.debug("find class: " + clsName);
				}
				return bind;
			}
		}
		/*if(isInput) {
			log.warn("can.not.find.input.cls.mapping.type:" + clsName);
		}else {
			log.warn("can.not.find.cls.mapping.type:" + clsName);
		}*/
		return null;
	}

	public Type getInputType(String name)throws Exception{
		Type type = ScalarType.parseScalarType(name);
		if(type != null){
			return type;
		}
		if(types.containsKey(name)){
			Type t = types.get(name);
			if((t instanceof InputObjectType)||(t instanceof EnumType)){
				return t;
			}
			throw new IntrospectionException("type.is.not.input.or.enum", name);
		}
		throw new IntrospectionException("can.not.find.type", name);
	}
	
	public Type getType(String name){
		return types.get(name);
	}

	public ObjectType getQueryType()throws Exception{
		Type type = types.get("Query");
		if(type == null){
			throw new IntrospectionException("not.find.query.type");
		}
		return (ObjectType)type;
	}

	public ObjectType getMutationType()throws Exception{
		Type type = types.get("Mutation");
		if(type == null){
			throw new IntrospectionException("not.find.mutation.type");
		}
		return (ObjectType)type;
	}
	
	protected void introspection()throws Exception{
		if(types.size() <= 0){
			throw new IntrospectionException("not.find.any.class.which.bind.annotation.of.GraphQL");
		}
		ObjectField[] fields;
		for(Type type : types.values()){
			if(type instanceof ObjectType){
				ObjectType ot = (ObjectType)type;
				fields = ot.getFields();
				checkObjectField(fields);
				checkObjectImplements(ot.getImplements(), fields);
			}else if(type instanceof InterfaceType){
				fields = ((InterfaceType)type).getFields();
				checkObjectField(fields);
			}else if(type instanceof InputObjectType){
				InputObjectField[] arguments = ((InputObjectType)type).getFields();
				checkInputFields(arguments);
			}else if(type instanceof EnumType){
				
			}else if(type instanceof UnionType){
				UnionType ut = (UnionType)type;
				for(String typeName : ut.getTypeNames()){
					if(!types.containsKey(typeName)){
						throw new IntrospectionException("can.not.find.type", typeName);
					}
					type = types.get(typeName);
					ut.setType(typeName, type);
				}
			}
		}
	}

	private void checkObjectImplements(String[] impls, ObjectField[] fields)throws Exception{
		if(impls == null){
			return;
		}
		for(String typeName : impls){
			if(!types.containsKey(typeName)){
				throw new IntrospectionException("can.not.find.type", typeName);
			}
			Type type = types.get(typeName);
			if(!(type instanceof InterfaceType)){
				throw new IntrospectionException("must.implements.interface.type", typeName);
			}
			InterfaceType it = (InterfaceType)type;
			for(ObjectField of : it.getFields()){
				if(!includeObjectField(of, fields)){
					throw new IntrospectionException("not.implements.interface.method", of.getName());
				}
			}
		}
	}

	private boolean includeObjectField(ObjectField of, ObjectField[] fields) {
		for(ObjectField field : fields){
			if(field.equals(of)){
				return true;
			}
		}
		return false;
	}

	private void checkObjectField(ObjectField[] fields)throws Exception{
		for(ObjectField field: fields){
			Argument[] arguments = field.getArguments();
			if(arguments != null){
				checkArguments(field.getMethod(), arguments);
			}
			checkObjectFieldType(field);
		}
	}
	
	private void checkArguments(Method method, Argument[] arguments)throws Exception{
		Class<?>[] paramTypes = method.getParameterTypes();
		java.lang.reflect.Type[] gtypes = method.getGenericParameterTypes();
		for(int i = 0; i < arguments.length; i++){
			Argument arg = arguments[i];
			Class<?> paramClass = paramTypes[i];
			java.lang.reflect.Type gtype = gtypes[i];
			Type type = arg.getType();
			Type ntype = replaceDummyType(arg.getName(), type);
			arg.setType(ntype);
			Type tp = getTypeByWrapperClass(paramClass, gtype, true);
			if(!ntype.equals(tp)){
				throw new IntrospectionException("diff.param.type", tp.getName(), type.getName());
			}
		}
	}
	
	private void checkInputFields(InputObjectField[] fields)throws Exception{
		for(InputObjectField field: fields){
			Type type = field.getType();
			Type ntype = replaceDummyType(field.getName(), type);
			field.setType(ntype);
			Method method = field.getMethod();
			Class<?> paramClass = method.getParameterTypes()[0];
			java.lang.reflect.Type gtype = method.getGenericParameterTypes()[0];
			Type tp = getTypeByWrapperClass(paramClass, gtype, true);
			if(!ntype.equals(tp)){
				throw new IntrospectionException("diff.input.param.type", tp.getName(), type.getName());
			}
		}
	}

	private void checkObjectFieldType(ObjectField field)throws Exception{
		Type type = field.getType();
		Type ntype = replaceDummyType(field.getName(), type);
		if(ntype != null){
			field.setType(ntype);
		}
		Method method = field.getMethod();
		Class<?> returnClass = method.getReturnType();
		java.lang.reflect.Type gtype = method.getGenericReturnType();
		Type tp = getTypeByWrapperClass(returnClass, gtype, false);
		if(!ntype.equals(tp)){
			throw new IntrospectionException("diff.return.type", tp.getName(), type.getName());
		}
	}
	
	private Type getTypeByWrapperClass(Class<?> paramClass, java.lang.reflect.Type paramType, boolean input)throws Exception{
		if(paramClass.toString().equals(paramType.toString())){
			return getTypeByClass(paramClass, input);
		}
		if(ArrayType.isArrayWrapperClass(paramClass)){
			Type t = ArrayType.buildNestedArrayType(null, paramClass, paramType, this, input);
			return t;
		}
		return getTypeByClass(paramClass, input);
	}
	
	private Type replaceDummyType(String name, Type type)throws Exception {
		String typeName;
		if(type instanceof DummyType){
			typeName = ((DummyType)type).getName();
			if(!types.containsKey(typeName)){
				throw new IntrospectionException("can.not.find.type", typeName);
			}
			type = types.get(typeName);
			if(type instanceof InputObjectType){
				throw new IntrospectionException("can.not.set.field", name, typeName);
			}
			return type;
		}
		if(type instanceof ArrayType){
			ArrayType tp = (ArrayType)type;
			Type rt = replaceDummyType(name, tp.getBaseType());
			return new ArrayType(rt, tp.getBindClass(), tp.isChildNotNull());
		}
		return type;
	}
	/*
	private String getClassNameByType(Type type)throws Exception{
		for(String name : binds.keySet()){
			ClassBind bind = binds.get(name);
			if(bind.getType().equals(type)){
				return bind.getClass().getName();
			}
		}
		throw new IntrospectionException("no.find.class.by.type", type.getName());
	}

	private boolean compatible(Class<?> cls, String bindClass){
		if(cls.getName().equals(bindClass)){
			return true;
		}
		Class<?> parent = cls.getSuperclass();
		if(parent.getName().equals(bindClass)){
			return true;
		}
		Class<?>[] infs = cls.getInterfaces();
		for(Class<?> inf : infs){
			if(inf.getName().equals(bindClass)){
				return true;
			}
		}
		return false;
	}*/
	
	public String toString(){
		StringBuffer sb = new StringBuffer();
		for(String name : types.keySet()){
			Type type = types.get(name);
			type.toSDL(sb);
		}
		return sb.toString();
	}
}
