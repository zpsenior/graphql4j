package graphql4j.type;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import graphql4j.operation.JSONParser;
import graphql4j.operation.JSONReader;
import graphql4j.exception.TransformException;

public class InputObjectType extends Type implements Input {
	
	private String name;
	private InputObjectField[] fields;
	
	public InputObjectType(String name, String bindClass, Collection<InputObjectField> fields){
		super(bindClass);
		this.name = name;
		this.fields = fields.toArray(new InputObjectField[fields.size()]);
	}
	
	public InputObjectField[] getFields(){
		return fields;
	}
	
	public InputObjectField getField(String name){
		for(InputObjectField arg : fields){
			if(arg.getName().equals(name)){
				return arg;
			}
		}
		return null;
	}
	
	@Override
	public void toSDL(StringBuffer sb) {
		sb.append("\n");
		sb.append(" @bind(\"").append(getBindClass()).append("\")");
		sb.append("\n");
		sb.append("input ").append(name);
		sb.append("{");
		sb.append("\n");
		for(InputObjectField arg : fields){
			sb.append("   ");
			arg.toString(sb);
			sb.append("\n");
		}
		sb.append("}");
	}

	@Override
	public String getName() {
		return name;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Object parseValue(Object value) throws Exception {
		Map map;
		if(value == null){
			map = null;
		}else if(value instanceof String){
			JSONReader reader = new JSONReader(new JSONParser((String)value));
			map = (Map)reader.readObject();
		}else{
			map = obj2Map(value);
		}
		Class<?> cls = Class.forName(getBindClass());
		if(cls.isInstance(map)){
			return map;
		}
		Object bean;
		try{
			bean = cls.newInstance();
		}catch(Throwable e){
			throw new TransformException("can.not.create.instance.without.params", getBindClass());
		}
		return trans2Bean(map, bean);
	}

	@SuppressWarnings("rawtypes")
	private Object trans2Bean(Map map, Object bean)throws Exception {
		for(InputObjectField field : fields){
			String name = field.getName();
			Type t = field.getType();
			Object val = map.get((Object)name);
			if(val == null){
				val = field.getDefaultValue();
			}
			if(val != null && !"".equals(val)){
				Object param = ((Input)t).parseValue(val);
				field.invokeSet(bean, param);
				continue;
			}
			if(field.isNotNull()){
				throw new TransformException("field.is.not.null", name);
			}
		}
		return bean;
	}

	@SuppressWarnings("rawtypes")
	private Map obj2Map(Object value)throws Exception{
		if(value instanceof Map){
			return (Map)value;
		}
		Map<String, Object> map = new HashMap<String, Object>();
		Class<?> cls = value.getClass();
		for(Method m : cls.getDeclaredMethods()){
			if(!Modifier.isPublic(m.getModifiers())){
				continue;
			}
			int len = m.getParameterTypes().length;
			String name = m.getName();
			Class<?> returnClass = m.getReturnType();
			if(len == 0 && !"void".equals(returnClass.getName()) && name.startsWith("get")){
				name = name.substring(3);
				name = name.substring(0, 1).toLowerCase() + name.substring(1);
				Object res = m.invoke(value);
				map.put(name, res);
			}
		}
		return map;
	}
	
	@Override
	public boolean compatible(Type t) {
		if(t instanceof InputObjectType){
			return name.equals(((InputObjectType)t).name);
		}
		return false;
	}

}
