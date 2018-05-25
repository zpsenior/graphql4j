package graphql4j.operation;

import graphql4j.exception.ExecuteException;
import graphql4j.type.Type;

public class ParamVariable extends ParamValue implements Comparable<ParamVariable>{
	
	private QueryArgument arg;

	public ParamVariable(QueryArgument arg) {
		this.arg = arg;
	}

	public String getVarName() {
		return arg.getName();
	}

	@Override
	public void toString(StringBuffer sb) {
		sb.append("$").append(arg.getName());
	}


	public int compareTo(ParamVariable o) {
		return arg.compareTo(o.arg);
	}

	public QueryArgument getArgument() {
		return arg;
	}

	@Override
	public Object getValue(Type tp) throws Exception {
		Type type = arg.getType();
		if(!tp.equals(type)){
			throw new ExecuteException("diff.variable.type", arg.getName(), tp.getName(), type.getName());
		}
		return arg.getValue();
	}
	
	
}
