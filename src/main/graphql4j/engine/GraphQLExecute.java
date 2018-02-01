package graphql4j.engine;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.Date;
//import java.util.HashSet;
import java.util.Map;
//import java.util.Set;

import graphql4j.operation.Entity;
import graphql4j.operation.Fragment;
import graphql4j.operation.GraphQL;
import graphql4j.exception.ExecuteException;
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
	private int deep = 0;
	private boolean format = false;
	
	private void print(Object... values){
		for(Object value : values){
			pw.print(value);
		}
	}
	
	private void println(Object value){
		pw.print(value);
		printSpace();
	}
	
	private void printStart(Object value){
		deep++;
		pw.print(value);
		printSpace();
	}
	
	private void printEnd(){
		deep--;
		printSpace();
	}

	private void printSpace() {
		if(!format){
			return;
		}
		pw.println();
		for(int i = 0; i < deep; i++){
			pw.print("\t");
		}
	}
	
	public void setFormat(boolean format){
		this.format = format;
	}

	public boolean isFormat() {
		return format;
	}

	public GraphQLExecute(GraphQLSchema schema){
		this.schema = schema;
	}

	public void query(GraphQL.Operation operation, Object rootQuery, PrintWriter pw)throws Exception{
		Type rootType = schema.getType("Query");
		execute(operation, rootQuery, rootType, pw);
	}

	public void mutation(GraphQL.Operation operation, Object rootQuery, PrintWriter pw)throws Exception{
		Type rootType = schema.getType("Mutation");
		execute(operation, rootQuery, rootType, pw);
	}

	public void execute(GraphQL.Operation operation, Object rootQbj, Type rootType, PrintWriter pw)throws Exception{
		this.pw = pw;
		if(operation == null){
			throw new ExecuteException("null.graphql.obj");
		}
		if(rootQbj == null){
			throw new ExecuteException("null.root.obj");
		}
		if(rootType == null){
			throw new ExecuteException("null.root.type");
		}
		
		if(!rootQbj.getClass().getName().equals(rootType.getBindClass())){
			throw new ExecuteException("root.obj.not.match.root.type");
		}
		printStart("{");
		Entity[] entities = operation.getEntities();
		boolean first = true;
		for(Entity entity : entities){
			if(!first){
				println(",");
			}
			printEntity(entity, rootQbj, rootType, operation.getFragments());
			first = false;
		}
		printEnd();
		print("}");
		pw.flush();
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
			throw new ExecuteException("not.find.fragment", name);
		}
		
		if(parent == null){
			throw new ExecuteException("entity.is.null");
		}
		
		print("\"", entity.getAlias(), "\"");
		print(":");
		
		String fieldName = entity.getName();
		
		ObjectField field = getField(parentType, fieldName);
		
		Map<String, ?> params = entity.getParamValues(field);
		
		Object result = field.invokeMethod(parent, params);
		
		if(result == null){
			if(field.isNotNull()){
				throw new ExecuteException("field.result.value.is.null", fieldName);
			}
			print("null");
			return;
		}
		
		Type fieldType = field.getType();
		Entity[] children = entity.getChildren();
		
		if(fieldType instanceof ScalarType){
			printScalarValue(result, (ScalarType)fieldType);
			return ;
		}else if(fieldType instanceof EnumType){
			printEnumValue(result, (EnumType)fieldType);
			return ;
		}else if(children == null || children.length <= 0){
			/*printAllField(result, fieldType);
			return ;*/
			throw new ExecuteException("can.not.print.all.object.entity.field", fieldName);
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
			print("\"", date.getTime(), "\"");
		}else if(objType == ScalarType.String){
			print("\"", obj, "\"");
		}else{
			print(obj);
		}
	}
	
	private void printEnumValue(Object obj, EnumType objType){
		print("'", obj, "'");
	}
	
	private void printArrayValue(Entity[] entitis, Object[] array, Type arrayType, Fragment[] fragments)throws Exception{
		Type objType = ((ArrayType)arrayType).getBaseType();
		printStart("[");
		boolean first = true;
		for(Object obj : array){
			if(!first){
				println(", ");
			}
			printObjectValue(entitis, obj, objType, fragments);
			first = false;
		}
		printEnd();
		print("]");
	}

	private void printObjectValue(Entity[] entitis, Object obj, Type objType, Fragment[] fragments) throws Exception {
		printStart("{");
		boolean first = true;
		for(Entity en : entitis){
			if(!first){
				println(",");
			}
			printEntity(en, obj, objType, fragments);
			first = false;
		}
		printEnd();
		print("}");
	}

	private void printFragment(Fragment fr, Object parent, Type parentType, Fragment[] fragments)throws Exception{
		Type mp = fr.getMappingType();
		if(parentType instanceof UnionType){
			UnionType ut = (UnionType)parentType;
			parentType = ut.cast(parent);
			if(!mp.equals(parentType)){
				throw new ExecuteException("diff.fragment.type.with.result.type", mp, parentType);
			}
		}else if(parentType instanceof InterfaceType || parentType instanceof ObjectType){
			if(mp.compatible(parentType)){
				Class<?> cls = Class.forName(mp.getBindClass());
				parent = cls.cast(parent);
			}else if(!parentType.compatible(mp)){
				throw new ExecuteException("uncompatible.fragment.type.with.result.type", mp, parentType);
			}
		}
		
		Entity[] entities = fr.getEntities();
		boolean first = true;
		for(Entity entity : entities){
			if(!first){
				println(",");
			}
			printEntity(entity, parent, parentType, fragments);
			first = false;
		}
	}

	/*
	private void printAllField(Object result, Type type) throws Exception {
		if(result == null){
			print("null");
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
		printStart("[");
		Object[] array;
		if(result instanceof Collection){
			array = ((Collection)result).toArray();
		}else if(result.getClass().isArray()){
			array = (Object[])result;
		}else{
			throw new ExecuteException("result.not.match.array.type", result.getClass(), type);
		}
		boolean first = true;
		for(Object obj : array){
			if(!first){
				println(",");
			}
			printAllField(obj, type);
			first = false;
		}
		printEnd();
		println("]");
	}

	private void printObjectTypeObj(Object result, ObjectType type) throws Exception {
		String[] names = getAllFieldNames(type);
		boolean first = true;
		printStart("{");
		for(String nm : names){
			ObjectField field = getField(type, nm);
			Type tp = field.getType();
			Object res = field.invokeMethod(result, null);
			if(!first){
				println(",");
			}
			print(nm, ":");
			printAllField(res, tp);
			first = false;
		}
		printEnd();
		print("}");
	}
	
	private String[] getAllFieldNames(Type t){
		ObjectField[] fields = getTypeFields(t);
		Set<String> names = new HashSet<String>();
		for(ObjectField field : fields){
			names.add(field.getName());
		}
		return names.toArray(new String[names.size()]);
	}*/

	private ObjectField[] getTypeFields(Type t) {
		ObjectField[] fields = null;
		if(t instanceof ObjectType){
			fields = ((ObjectType)t).getFields();
		}else if(t instanceof InterfaceType){
			fields = ((InterfaceType)t).getFields();
		}
		return fields;
	}
	
	private ObjectField getField(Type tp, String fieldName)throws Exception{
		ObjectField[] fields = getTypeFields(tp);
		if(fields != null){
			for(ObjectField field : fields){
				if(field.getName().equals(fieldName)){
					return field;
				}
			}
		}
		throw new ExecuteException("not.bind.field.name", fieldName, tp.getName());
	}
}
