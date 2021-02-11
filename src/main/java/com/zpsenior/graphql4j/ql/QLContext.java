package com.zpsenior.graphql4j.ql;

import java.util.HashMap;
import java.util.Map;

import com.zpsenior.graphql4j.JoinExecutor;
import com.zpsenior.graphql4j.ParamFinder;
import com.zpsenior.graphql4j.input.InputType;

public final class QLContext {
	
	private ParamFinder<?> finder;
	private JoinExecutor joiner;
	
	private Map<String, Object> params = new HashMap<>();
	
	public QLContext(ParamFinder<?> finder, JoinExecutor joiner) {
		this.finder = finder;
		this.joiner = joiner;
	}
	
	public void bindVariable(EntryArgument[] arguments) throws Exception{
		params.clear();
		for(EntryArgument arg : arguments) {
			String name = arg.getName();
			InputType type = arg.getType();
			Object value = finder.getParam(name, type);
			params.put(name, value);
		}
	}

	public Object getParamValue(String name) {
		return params.get(name);
	}

	public Object call(String method, Object[] values, Class<?> resultType) throws Exception{
		return joiner.call(method, values, resultType);
	}
}
