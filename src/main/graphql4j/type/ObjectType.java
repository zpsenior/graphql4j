package graphql4j.type;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ObjectType extends Type {
	
	private String name;
	private Set<String> impls;
	private Set<ObjectField> fields = new HashSet<ObjectField>();
	
	public ObjectType(String name, Set<String> impls, String bindClass, Collection<ObjectField> fields){
		super(bindClass);
		this.name = name;
		if(impls != null){
			this.impls = new HashSet<String>(impls);
		}
		this.fields.addAll(fields);
	}
	
	public String getName() {
		return name;
	}
	
	public String[] getImplements(){
		if(impls == null){
			return null;
		}
		return impls.toArray(new String[impls.size()]);
	}
	
	public ObjectField[] getFields(){
		return fields.toArray(new ObjectField[fields.size()]);
	}
	
	public ObjectField getField(String name)throws Exception{
		for(ObjectField field : fields){
			if(field.getName().equals(name)){
				return field;
			}
		}
		return null;
	}
	
	public void toSDL(StringBuffer sb) {
		sb.append("\n");
		sb.append("@bind(\"").append(getBindClass()).append("\")");
		sb.append("\n");
		sb.append("type ").append(name).append(" ");
		if(impls != null && impls.size() > 0){
			sb.append("implements ");
			for(String impl : impls){
				sb.append(impl).append(" ");
			}
		}
		sb.append("{");
		sb.append("\n");
		for(ObjectField field : fields){
			sb.append("   ");
			field.toString(sb);
			sb.append("\n");
		}
		sb.append("}");
	}
	
	@Override
	public boolean compatible(Type t) {
		if(t instanceof ObjectType){
			if(name.equals(((ObjectType)t).name)){
				return true;
			}
			if(impls != null){
				for(String impl : impls){
					if(t.getName().equals(impl)){
						return true;
					}
				}
			}
		}
		return false;
	}

}
