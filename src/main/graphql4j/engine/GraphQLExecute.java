package graphql4j.engine;

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
	
	private StringBuffer sb;
	
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
		sb = new StringBuffer();
		sb.append("{");
		Entity[] entities = operation.getEntities();
		boolean first = true;
		for(Entity entity : entities){
			if(!first){
				sb.append(",");
			}
			printEntity(entity, rootQbj, rootType, operation.getFragments());
			first = false;
		}
		sb.append("}");
		return sb.toString();
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
		sb.append(entity.getAlias());
		sb.append(":");
		
		if(parent == null){
			sb.append("null");
			return;
		}
		
		String fieldName = entity.getName();
		
		ObjectField field = getField(parentType, fieldName);
		
		Map<String, ?> params = entity.getParamValues(field);
		
		Object result = field.invokeMethod(parent, params);
		
		if(result == null){
			if(field.isNotNull()){
				throw new BindException("field.result.value.is.null", fieldName);
			}
			sb.append("null");
			return;
		}
		
		Type fieldType = field.getType();
		
		if(fieldType instanceof ScalarType){
			if(fieldType == ScalarType.Date){
				Date date = (Date)result;
				sb.append("'").append(date.getTime()).append("'");
			}else{
				sb.append("'").append(result).append("'");
			}
			return ;
		}else if(fieldType instanceof EnumType){
			sb.append("'").append(result).append("'");
			return ;
		}
		
		Entity[] children = entity.getChildren();
		if(children == null || children.length <= 0){
			printAllField(fieldType, result);
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
	
	private void printArrayValue(Entity[] entitis, Object[] array, Type parentType, Fragment[] fragments)throws Exception{
		parentType = ((ArrayType)parentType).getBaseType();
		sb.append("[");
		boolean first = true;
		for(Object obj : array){
			if(!first){
				sb.append(", ");
			}
			printObjectValue(entitis, obj, parentType, fragments);
			first = false;
		}
		sb.append("]");
	}

	private void printObjectValue(Entity[] entitis, Object obj, Type parentType, Fragment[] fragments) throws Exception {
		sb.append("{");
		boolean first = true;
		for(Entity en : entitis){
			if(!first){
				sb.append(",");
			}
			printEntity(en, obj, parentType, fragments);
			first = false;
		}
		sb.append("}");
	}

	private void printFragment(Fragment fr, Object parent, Type parentType, Fragment[] fragments)throws Exception{
		if(parentType instanceof UnionType){
			UnionType ut = (UnionType)parentType;
			parentType = ut.cast(parent);
		}
		Entity[] entities = fr.getEntities();
		boolean first = true;
		for(Entity entity : entities){
			if(!first){
				sb.append(",");
			}
			printEntity(entity, parent, parentType, fragments);
			first = false;
		}
	}

	
	private void printAllField(Type type, Object result) throws Exception {
		if(result == null){
			sb.append("null");
			return;
		}
		if(type == ScalarType.Date){
			Date date = (Date)result;
			sb.append("'");
			sb.append(date.getTime());
			sb.append("'");
			return;
		}else if(type instanceof ScalarType){
			sb.append("'");
			sb.append(result);
			sb.append("'");
			return;
		}else if(type instanceof ArrayType){
			printArrayTypeObj((ArrayType)type, result);
			return;
		}else if(type instanceof ObjectType){
			printObjectTypeObj((ObjectType)type, result);
			return;
		}else if(type instanceof UnionType){
			UnionType ut = (UnionType)type;
			ObjectType ot = ut.matchType(result);
			printObjectTypeObj(ot, result);
			return;
		}else if(type instanceof EnumType){
			sb.append("'");
			sb.append(result);
			sb.append("'");
			return;
		}
	}
	
	@SuppressWarnings("rawtypes")
	private void printArrayTypeObj(ArrayType arrayType, Object result) throws Exception {
		Type type = arrayType.getBaseType();
		sb.append("[");
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
				sb.append(",");
			}
			printAllField(type, obj);
			first = false;
		}
		sb.append("]");
	}

	private void printObjectTypeObj(ObjectType type, Object result) throws Exception {
		String[] names = getAllFieldNames(type);
		boolean first = true;
		sb.append("{");
		for(String nm : names){
			ObjectField field = getField(type, nm);
			Type tp = field.getType();
			Object res = field.invokeMethod(result, null);
			if(!first){
				sb.append(",");
			}
			sb.append(nm).append(":");
			printAllField(tp, res);
			first = false;
		}
		sb.append("}");
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
