package graphql4j.operation;

import java.util.Map;

import graphql4j.exception.TransformException;
import graphql4j.type.InputObjectField;
import graphql4j.type.InputObjectType;
import graphql4j.type.Type;

public class ParamObject extends ParamValue {
	
	private Map<String, ParamValue> values;

	ParamObject(Map<String, ParamValue> map) {
		this.values = map;
	}

	@Override
	public Object getValue(Type tp) throws Exception {
		if(!(tp instanceof InputObjectType)){
			throw new TransformException("not.match.input.type");
		}
		InputObjectType type = (InputObjectType)tp;
		Class<?> cls = Class.forName(type.getBindClass());
		Object bean = cls.newInstance();
		for(String name : values.keySet()){
			InputObjectField field = type.getField(name);
			if(field == null){
				throw new TransformException("not.find.field", name);
			}
			ParamValue pv = values.get(name);
			Object v = pv.getValue(field.getType());
			field.invokeSet(bean, v);
		}
		return bean;
	}

	@Override
	public void toString(StringBuffer sb) {
		sb.append("{");
		boolean first = true;
		for(String key : values.keySet()){
			ParamValue pv = values.get(key);
			if(!first){
				sb.append(", ");
			}
			sb.append(key).append(":");
			pv.toString(sb);
			first = false;
		}
		sb.append("}");
	}

}
