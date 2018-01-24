package graphql4j.type;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import graphql4j.exception.BindException;
import graphql4j.exception.TransformException;

public class UnionType extends Type {
	
	private String name;
	private Map<String, ObjectType> types = new HashMap<String, ObjectType>();
	
	public UnionType(String name, Collection<String> typeNames){
		super("java.lang.Object");
		this.name = name;
		for(String typeName : typeNames){
			types.put(typeName, null);
		}
	}

	public Type cast(Object obj)throws Exception{
		for(String name : types.keySet()){
			ObjectType ot = types.get(name);
			Class<?> cls = Class.forName(ot.getBindClass());
			if(cls.isInstance(obj)){
				return ot;
			}
		}
		throw new BindException("not.match.any.type.in.the.union", obj);
	}

	@Override
	public void toSDL(StringBuffer sb) {
		sb.append("\n");
		sb.append("union ").append(name).append("=");
		boolean first = true;
		for(String type : types.keySet()){
			if(!first){
				sb.append("|");
			}
			sb.append(" ").append(type).append(" ");
			first = false;
		}
	}

	@Override
	public String getName() {
		return name;
	}

	public Type[] getTypes() {
		return types.values().toArray(new Type[types.size()]);
	}

	public String[] getTypeNames() {
		return types.keySet().toArray(new String[types.size()]);
	}

	protected void setType(String typeName, Type type)throws Exception {
		if(types.get(typeName) != null){
			throw new TransformException("union.not.include.the.child", typeName);
		}
		if(type instanceof ScalarType){
			throw new TransformException("can.not.set.union.as.scalar.type", name, typeName);
		}
		if(type instanceof InputObjectType){
			throw new TransformException("can.not.set.union.as.input.type", name, typeName);
		}
		if(type instanceof InterfaceType){
			throw new TransformException("can.not.set.union.as.interface.type", name, typeName);
		}
		if(type instanceof EnumType){
			throw new TransformException("can.not.set.union.as.enum.type", name, typeName);
		}
		if(type instanceof UnionType){
			throw new TransformException("can.not.set.union.as.union.type", name, typeName);
		}
		types.put(typeName, (ObjectType)type);
	}

	public ObjectType matchType(Object result)throws Exception{
		for(String name : types.keySet()){
			ObjectType type = types.get(name);
			Class<?> cls = Class.forName(type.getBindClass());
			if(cls.isInstance(result)){
				return type;
			}
		}
		throw new TransformException("object.can.not.tranform.to.union.types", name);
	}

	@Override
	public boolean compatible(Type t) {
		for(String name : types.keySet()){
			ObjectType type = types.get(name);
			if(type.compatible(t)){
				return true;
			}
		}
		return false;
	}

}
