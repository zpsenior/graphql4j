package com.zpsenior.graphql4j.ql;

import java.util.HashMap;
import java.util.Map;

import com.zpsenior.graphql4j.JoinExecutor;
import com.zpsenior.graphql4j.exception.CompileException;
import com.zpsenior.graphql4j.schema.Schema;

public class QLRoot {
	
	private Map<String, Entry> entries = new HashMap<>();
	
	protected void add(Entry entry)throws Exception {
		String name = entry.getName();
		if(entries.containsKey(name)) {
			throw new CompileException("entry(" + name + ") had been included");
		}
		entries.put(name, entry);
	}
	
	public Object query(QLContext context, Object query, String entryName) throws Exception{
		Entry entry = entries.get(entryName);
		return entry.execute(context, query);
	}
	

	public Object mutation(QLContext context, Object mutation, String entryName) throws Exception{
		Entry entry = entries.get(entryName);
		return entry.execute(context, mutation);
	}
	
	public void bind(Schema schema, JoinExecutor joinExecutor) throws Exception{
		for(Entry entry : entries.values()) {
			entry.bind(schema, joinExecutor);
		}
	}
	
	public Entry getEntry(String name) {
		return entries.get(name);
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for(Entry entry : entries.values()) {
			sb.append(entry).append("\n");
		}
		return sb.toString();
	}
}
