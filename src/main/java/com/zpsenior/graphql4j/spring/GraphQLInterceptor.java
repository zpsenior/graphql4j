package com.zpsenior.graphql4j.spring;

import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zpsenior.graphql4j.ParamFinder;
import com.zpsenior.graphql4j.exception.ExecuteException;
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

public abstract class GraphQLInterceptor extends HandlerInterceptorAdapter {
	
	private Schema schema;
	
	private QLRoot root = new QLRoot();
	
	private SpringJoinExecutor joinExecutor;
	
	private Class<?> queryClass;
	private Class<?> mutationClass;
	
	private Object query;
	private Object mutation;
	
	private boolean bind = false;
	
	private void bind(ApplicationContext ctx)throws Exception {
		if(queryClass == null || mutationClass == null) {
			throw new ExecuteException("can not call init method at first!");
		}
		joinExecutor = new SpringJoinExecutor(ctx);
		this.query = ctx.getBean(queryClass);
		this.mutation = ctx.getBean(mutationClass);
		
		root.bind(schema, joinExecutor);
		bind = true;
	}
	
	public void init(String queryClassName, String mutationClassName, String inputClassPackages, String qlFileName)throws Exception {
		
		this.queryClass = Class.forName(queryClassName);
		this.mutationClass = Class.forName(mutationClassName);
		
		InputClassFinder finder = new InputClassFinder(inputClassPackages.split(","));
		schema = new Schema(queryClass, mutationClass);
		
		QLBuilder builder = new QLBuilder();
		
		InputStreamReader fr = new InputStreamReader(this.getClass().getClassLoader().getResource(qlFileName).openStream());
		
		builder.build(fr, finder, root);
	}
	
	protected abstract ParamFinder<?> buildParamFinder(HttpServletRequest request)throws Exception;
	
	protected abstract void checkPermission(HttpServletRequest request, String entryName)throws Exception;

	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		ParamFinder<?> paramFinder = null;
		if(!bind) {
			ApplicationContext ctx = RequestContextUtils.findWebApplicationContext(request);
			bind(ctx);
		}
		
		String entryName = getEntryName(request);
		
		try{
			checkPermission(request, entryName);
			paramFinder = buildParamFinder(request);
		}catch(Exception e) {
			response.getWriter().println(new Result(Result.ERROR, e));
			return false;
		}
		
		if("schema".equals(entryName)) {
			response.getWriter().println(schema.toString());
			return false;
		}
		
		if("graphql".equals(entryName)) {
			response.getWriter().println(root.toString());
			return false;
		}
		
		Result result = doHandle(entryName, paramFinder);
		
		String json = (new ObjectMapper()).writeValueAsString(result);
		
		response.getWriter().println(json);
		return false;
	}

	private Result doHandle(String entryName, ParamFinder<?> finder) {
		
		Object resultValue;
		
		try {
			QLContext context = new QLContext(finder, joinExecutor);
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
		if(pos >= 0) {
			return url.substring(pos + 1);
		}
		return null;
	}
}
