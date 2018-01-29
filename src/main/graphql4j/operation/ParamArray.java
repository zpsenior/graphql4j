package graphql4j.operation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import graphql4j.exception.ExecuteException;
import graphql4j.type.ArrayType;
import graphql4j.type.Type;

public class ParamArray extends ParamValue {
	
	private List<ParamValue> values;

	ParamArray(List<ParamValue> values) {
		this.values = values;
	}

	@Override
	public void toString(StringBuffer sb) {
		sb.append("[");
		boolean first = true;
		for(ParamValue pv : values){
			if(!first){
				sb.append(", ");
			}
			pv.toString(sb);
			first = false;
		}
		sb.append("]");
	}

	@Override
	public Object getValue(Type tp) throws Exception {
		if(!(tp instanceof ArrayType)){
			throw new ExecuteException("not.match.list.type");
		}
		ArrayType lt = (ArrayType)tp;
		Type type = lt.getBaseType();
		Collection<Object> list = buildListObject(lt);
		for(ParamValue pv : values){
			list.add(pv.getValue(type));
		}
		if("".equals(lt.getBindClass())){
			return list.toArray();
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	public Collection<Object> buildListObject(ArrayType lt)throws Exception{
		String className = lt.getBindClass();
		if("".equals(className)){
			return new ArrayList<Object>();
		}
		Class<?> cls = Class.forName(className);
		return (Collection<Object>)cls.newInstance();
	}

}
