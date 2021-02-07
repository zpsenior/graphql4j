package com.zpsenior.graphql4j.ql;

import java.util.HashMap;
import java.util.Map;

import com.zpsenior.graphql4j.JoinExecutor;
import com.zpsenior.graphql4j.ParamFinder;
import com.zpsenior.graphql4j.schema.Schema;

public class QLRoot {
	
	private Object query;
	private Object mutation;
	
	private Map<String, Entry> entries = new HashMap<>();
	
	public void add(Entry entry) {
		String name = entry.getName();
		if(entries.containsKey(name)) {
			throw new RuntimeException("entry(" + name + ") had been included");
		}
		entries.put(name, entry);
	}
	
	public Object query(String entryName, ParamFinder finder, JoinExecutor joiner) throws Exception{
		Entry entry = entries.get(entryName);
		QLContext context = new QLContext(finder, joiner);
		return entry.execute(context, query);
	}
	

	public Object mutation(String entryName, ParamFinder finder, JoinExecutor joiner) throws Exception{
		Entry entry = entries.get(entryName);
		QLContext context = new QLContext(finder, joiner);
		return entry.execute(context, mutation);
	}
	
	public void bind(Schema schema) throws Exception{
		this.query = schema.getQuery();
		this.mutation = schema.getMutation();
		for(Entry entry : entries.values()) {
			entry.bind(schema);
		}
	}
}
