package com.zpsenior.graphql4j.spring;

import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zpsenior.graphql4j.ql.Entry;
import com.zpsenior.graphql4j.ql.EntryKind;
import com.zpsenior.graphql4j.ql.QLBuilder;
import com.zpsenior.graphql4j.ql.QLContext;
import com.zpsenior.graphql4j.ql.QLRoot;
import com.zpsenior.graphql4j.schema.Schema;
import com.zpsenior.graphql4j.utils.InputClassFinder;

import java.io.InputStreamReader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class GraphQLInterceptor extends HandlerInterceptorAdapter {
	
	private Schema schema;
	
	private QLRoot root = new QLRoot();
	
	private SpringJoinExecutor joinExecutor;
	
	private Object query;
	private Object mutation;
	
	public GraphQLInterceptor(Object query, Object mutation, String inputClassPackage, String qlFileName)throws Exception {
		
		this.query = query;
		this.mutation = mutation;
		
		Class<?> queryClass = query.getClass();
		Class<?> mutationClass = mutation.getClass();
		
		InputClassFinder finder = new InputClassFinder(inputClassPackage);
		schema = new Schema(queryClass, mutationClass);
		
		QLBuilder builder = new QLBuilder();
		
		InputStreamReader fr = new InputStreamReader(this.getClass().getClassLoader().getResource(qlFileName).openStream());
		
		builder.build(fr, finder, root);
		
		root.bind(schema);
	}

	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		
		String entryName = getEntryName(request);
		
		JsonNode jroot = (new ObjectMapper()).readTree(request.getInputStream());
		
		try{
			validate(request, entryName, jroot);
		}catch(Exception e) {
			response.getWriter().println(new Result(Result.ERROR, e));
			return true;
		}
		
		if("schema".equals(entryName)) {
			response.getWriter().println(schema.toString());
			return true;
		}
		
		if("graphql".equals(entryName)) {
			response.getWriter().println(root.toString());
			return true;
		}
		
		if(joinExecutor == null) {
			ApplicationContext ctx = RequestContextUtils.findWebApplicationContext(request);
			joinExecutor = new SpringJoinExecutor(ctx);
		}
		
		Result result = doHandle(entryName, jroot);
		
		String json = (new ObjectMapper()).writeValueAsString(result);
		
		response.getWriter().println(json);
		return true;
	}

	protected void validate(HttpServletRequest request, String entryName, JsonNode jroot) throws Exception{
		// TODO Auto-generated method stub
		
	}

	private Result doHandle(String entryName, JsonNode jroot) {
		
		Object resultValue;
		
		try {
			QLContext context = new QLContext(new JsonParamFinder(jroot), joinExecutor);
			Entry entry = root.getEntry(entryName);
			resultValue = entry.execute(context, entry.getKind() == EntryKind.Query ? query : mutation);
			return new Result(resultValue);
		}catch(Exception e) {
			return new Result(Result.ERROR, e);
		}
	}
	
	private String getEntryName(HttpServletRequest request){
		String url = request.getRequestURI();
		int pos = url.lastIndexOf('/');
		if(pos > 0) {
			return url.substring(pos + 1);
		}
		return null;
	}
}
