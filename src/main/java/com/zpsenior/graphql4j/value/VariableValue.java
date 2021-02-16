package com.zpsenior.graphql4j.value;

import com.zpsenior.graphql4j.ql.QLContext;

public class VariableValue extends Value {
	
	private String varName;
	
	public VariableValue(String varName) {
		this.varName = varName;
	}

	public String getVarName() {
		return varName;
	}

	@Override
	public Object getValue(QLContext context) {
		return context.getParamValue(varName);
	}

	public void toString(StringBuffer sb) {
		sb.append("$").append(varName);
	}
}
