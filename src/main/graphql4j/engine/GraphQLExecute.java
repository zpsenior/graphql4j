package graphql4j.engine;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import graphql4j.operation.Entity;
import graphql4j.operation.Fragment;
import graphql4j.operation.GraphQL;
import graphql4j.exception.BindException;
import graphql4j.exception.IntrospectionException;
import graphql4j.type.EnumType;
import graphql4j.type.GraphQLSchema;
import graphql4j.type.ArrayType;
import graphql4j.type.InterfaceType;
import graphql4j.type.ObjectField;
import graphql4j.type.ObjectType;
import graphql4j.type.ScalarType;
import graphql4j.type.Type;
import graphql4j.type.UnionType;

public class GraphQLExecute {
	
	private GraphQLSchema schema;
	
	private PrintWriter pw;
	
	public GraphQLExecute(GraphQLSchema schema){
		this.schema = schema;
	}

	public String query(GraphQL.Operation operation, Object rootQuery)throws Exception{
		Type rootType = schema.getType("Query");
		return execute(operation, rootQuery, rootType);
	}

	public String mutation(GraphQL.Operation operation, Object rootQuery)throws Exception{
		Type rootType = schema.getType("Mutation");
		return execute(operation, rootQuery, rootType);
	}

	private String execute(GraphQL.Operation operation, Object rootQbj, Type rootType)throws Exception{
		if(operation == null){
			throw new IntrospectionException("null.graphql.obj");
		}
		if(rootQbj == null){
			throw new IntrospectionException("null.root.obj");
		}
		if(rootType == null){
			throw new IntrospectionException("null.root.type");
		}
		
		if(!rootQbj.getClass().getName().equals(rootType.getBindClass())){
			throw new IntrospectionException("root.obj.not.match.root.type");
		}
		StringWriter sw = new StringWriter();
		pw = new PrintWriter(sw);
		pw.println("{");
		Entity[] entities = operation.getEntities();
		boolean first = true;
		for(Entity entity : entities){
			if(!first){
				pw.print(",");
			}
			printEntity(entity, rootQbj, rootType, operation.getFragments());
			first = false;
		}
		pw.println("}");
		pw.flush();
		return sw.toString();
	}
	
	private void printEntity(Entity entity, Object parent, Type parentType, Fragment[] fragments)throws Exception{
		if(entity.isFragment()){
			String name = entity.getName();
			if(fragments != null && fragments.length > 0){
				for(Fragment fr : fragments){
					if(fr.getName().equals(name)){
						printFragment(fr, parent, parentType, fragments);
						return;
					}
				}
			}
			throw new IntrospectionException("not.find.fragment", name);
		}
		
		if(parent == null){
			throw new IntrospectionException("entity.is.null");
		}
		
		pw.print(entity.getAlias());
		pw.print(":");
		
		String fieldName = entity.getName();
		
		ObjectField field = getField(parentType, fieldName);
		
		Map<String, ?> params = entity.getParamValues(field);
		
		Object result = field.invokeMethod(parent, params);
		
		if(result == null){
			if(field.isNotNull()){
				throw new BindException("field.result.value.is.null", fieldName);
			}
			pw.print("null");
			return;
		}
		
		Type fieldType = field.getType();
		
		if(fieldType instanceof ScalarType){
			printScalarValue(result, (ScalarType)fieldType);
			return ;
		}else if(fieldType instanceof EnumType){
			printEnumValue(result, (EnumType)fieldType);
			return ;
		}
		
		Entity[] children = entity.getChildren();
		if(children == null || children.length <= 0){
			printAllField(result, fieldType);
			return ;
		}
		
		if(result instanceof Collection){
			Collection<?> collections = (Collection<?>)result;
			printArrayValue(children, collections.toArray(), fieldType, fragments);
		}else if(result.getClass().isArray()){
			Object[] array = (Object[])result;
			printArrayValue(children, array, fieldType, fragments);
		}else{
			printObjectValue(children, result, fieldType, fragments);
		}
	}
	
	private void printScalarValue(Object obj, ScalarType objType){
		if(objType == ScalarType.Date){
			Date date = (Date)obj;
			pw.print("'");
			pw.print(date.getTime());
			pw.print("'");
		}else{
			pw.print("'");
			pw.print(obj);
			pw.print("'");
		}
		return ;
	}
	
	private void printEnumValue(Object obj, EnumType objType){
		pw.print("'");
		pw.print(obj);
		pw.print("'");
		return ;
	}
	
	private void printArrayValue(Entity[] entitis, Object[] array, Type arrayType, Fragment[] fragments)throws Exception{
		Type objType = ((ArrayType)arrayType).getBaseType();
		pw.print("[");
		boolean first = true;
		for(Object obj : array){
			if(!first){
				pw.print(", ");
			}
			printObjectValue(entitis, obj, objType, fragments);
			first = false;
		}
		pw.print("]");
	}

	private void printObjectValue(Entity[] entitis, Object obj, Type objType, Fragment[] fragments) throws Exception {
		pw.print("{");
		boolean first = true;
		for(Entity en : entitis){
			if(!first){
				pw.print(",");
			}
			printEntity(en, obj, objType, fragments);
			first = false;
		}
		pw.print("}");
	}

	private void printFragment(Fragment fr, Object parent, Type parentType, Fragment[] fragments)throws Exception{
		Type mp = fr.getMappingType();
		if(parentType instanceof UnionType){
			UnionType ut = (UnionType)parentType;
			parentType = ut.cast(parent);
			if(!mp.equals(parentType)){
				throw new BindException("diff.fragment.type.with.result.type", mp, parentType);
			}
		}else if(parentType instanceof InterfaceType || parentType instanceof ObjectType){
			if(mp.compatible(parentType)){
				Class<?> cls = Class.forName(mp.getBindClass());
				parent = cls.cast(parent);
			}else if(!parentType.compatible(mp)){
				throw new BindException("uncompatible.fragment.type.with.result.type", mp, parentType);
			}
		}
		
		Entity[] entities = fr.getEntities();
		boolean first = true;
		for(Entity entity : entities){
			if(!first){
				pw.print(",");
			}
			printEntity(entity, parent, parentType, fragments);
			first = false;
		}
	}

	
	private void printAllField(Object result, Type type) throws Exception {
		if(result == null){
			pw.print("null");
			return;
		}
		if(type instanceof ScalarType){
			printScalarValue(result, (ScalarType)type);
			return;
		}else if(type instanceof ArrayType){
			printArrayTypeObj(result, (ArrayType)type);
			return;
		}else if(type instanceof ObjectType){
			printObjectTypeObj(result, (ObjectType)type);
			return;
		}else if(type instanceof UnionType){
			UnionType ut = (UnionType)type;
			ObjectType ot = ut.matchType(result);
			printObjectTypeObj(result, ot);
			return;
		}else if(type instanceof EnumType){
			printEnumValue(result, (EnumType)type);
			return;
		}
	}
	
	@SuppressWarnings("rawtypes")
	private void printArrayTypeObj(Object result, ArrayType arrayType) throws Exception {
		Type type = arrayType.getBaseType();
		pw.print("[");
		Object[] array;
		if(result instanceof Collection){
			array = ((Collection)result).toArray();
		}else if(result.getClass().isArray()){
			array = (Object[])result;
		}else{
			throw new BindException("result.not.match.array.type", result.getClass(), type);
		}
		boolean first = true;
		for(Object obj : array){
			if(!first){
				pw.print(",");
			}
			printAllField(obj, type);
			first = false;
		}
		pw.print("]");
	}

	private void printObjectTypeObj(Object result, ObjectType type) throws Exception {
		String[] names = getAllFieldNames(type);
		boolean first = true;
		pw.print("{");
		for(String nm : names){
			ObjectField field = getField(type, nm);
			Type tp = field.getType();
			Object res = field.invokeMethod(result, null);
			if(!first){
				pw.print(",");
			}
			pw.print(nm);
			pw.print(":");
			printAllField(res, tp);
			first = false;
		}
		pw.print("}");
	}
	
	private String[] getAllFieldNames(Type t){
		ObjectField[] fields = getTypeFields(t);
		Set<String> names = new HashSet<String>();
		for(ObjectField field : fields){
			names.add(field.getName());
		}
		return names.toArray(new String[names.size()]);
	}

	private ObjectField[] getTypeFields(Type t) {
		ObjectField[] fields = null;
		if(t instanceof ObjectType){
			fields = ((ObjectType)t).getFields();
		}else if(t instanceof InterfaceType){
			fields = ((InterfaceType)t).getFields();
		}
		return fields;
	}
	
	public ObjectField getField(Type tp, String fieldName)throws Exception{
		ObjectField[] fields = getTypeFields(tp);
		if(fields != null){
			for(ObjectField field : fields){
				if(field.getName().equals(fieldName)){
					return field;
				}
			}
		}
		throw new BindException("not.bind.field.name", fieldName, tp.getName());
	}
}
