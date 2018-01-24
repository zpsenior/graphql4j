package graphql4j.type;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import graphql4j.exception.TransformException;

public class EnumType extends Type implements Input {
	
	private String name;
	private Set<String> fields = new HashSet<String>();
	
	public EnumType(String name, String bindClass, Collection<String> fields){
		super(bindClass);
		this.name = name;
		this.fields.addAll(fields);
	}
	
	@Override
	public boolean compatible(Type t) {
		if(t instanceof EnumType){
			return name.equals(((EnumType)t).name);
		}
		return false;
	}
	

	@Override
	public void toSDL(StringBuffer sb) {
		sb.append("\n");
		sb.append("@bind(\"").append(getBindClass()).append("\")");
		sb.append("\n");
		sb.append("enum ").append(name);
		sb.append("{");
		sb.append("\n");
		for(String field : fields){
			sb.append(field);
			sb.append("\n");
		}
		sb.append("}");
	}

	@Override
	public String getName() {
		return name;
	}
	
	public String[] getFields(){
		return fields.toArray(new String[fields.size()]);
	}


	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Object parseValue(Object value) throws Exception {
		Class cls = Class.forName(getBindClass());
		if(value != null && value instanceof String){
			return Enum.valueOf(cls, (String)value);
		}
		throw new TransformException("can.not.transform.to.enum.type", value);
	}

}
