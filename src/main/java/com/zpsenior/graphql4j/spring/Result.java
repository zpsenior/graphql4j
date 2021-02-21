package com.zpsenior.graphql4j.spring;

import java.io.PrintWriter;
import java.io.StringWriter;

public class Result {
	
	public final static int ERROR = -1000;
	
	private int code = 0;
	
	private String msg;
	
	private Object data;
	
	public Result(Object data) {
		this.data = data;
	}
	
	public Result(int code, Throwable e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		pw.flush();
		this.code = code;
		this.msg = sw.toString();
	}

	public int getCode() {
		return code;
	}

	public String getMsg() {
		return msg;
	}

	public Object getData() {
		return data;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("{");
		sb.append("\"code\":\"").append(code).append("\"");
		sb.append(",\"msg\":\"").append(msg).append("\"");
		sb.append(",\"data\":\"").append(data).append("\"");
		sb.append("}");
		return sb.toString();
	}

}
