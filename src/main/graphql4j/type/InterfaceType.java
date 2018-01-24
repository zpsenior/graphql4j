package graphql4j.type;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class InterfaceType extends Type {
	
	private String name;
	private Set<ObjectField> fields = new HashSet<ObjectField>();
	
	public InterfaceType(String name, String bindClass, Collection<ObjectField> fields){
		super(bindClass);
		this.name = name;
		this.fields.addAll(fields);
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

	@Override
	public void toSDL(StringBuffer sb) {
		sb.append("\n");
		sb.append("@bind(\"").append(getBindClass()).append("\")");
		sb.append("\n");
		sb.append("interface ").append(name);
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
	public String getName() {
		return name;
	}

	
	@Override
	public boolean compatible(Type t) {
		if(t instanceof InterfaceType){
			return name.equals(((InterfaceType)t).name);
		}
		return false;
	}
}
