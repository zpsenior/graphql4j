package com.zpsenior.graphql4j.utils;

import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.zpsenior.graphql4j.schema.Member;
import com.zpsenior.graphql4j.schema.Schema;
import com.zpsenior.graphql4j.schema.TypeConfig;

public class CRUDBuilder extends SQLBuilder {
	
	private Schema schema;
	
	public CRUDBuilder(Schema schema) {
		this.schema = schema;
	}
	
	public void build(PrintWriter pw, Class<?> query, Class<?> mutation, Filter filter)throws Exception {
		for(String name : schema.getTypeNames()) {
			if(filter!= null && filter.filterType(name)) {
				continue;
			}
			TypeConfig tc = schema.getTypeConfig(name);
			String className = tc.getBindClass().getName();
			String incr = tc.getAnn().incr();
			if(filter!= null) {
				name = filter.mapTableName(name, className);
			}
			List<String> fields = new ArrayList<>();
			Set<String> keys = new LinkedHashSet<>();
			for(Member member : tc.getMembers()) {
				if(member.isMethod()|| member.getJoin() != null) {
					continue;
				}
				String fieldName = member.getName();
				if(member.getField().isKey()) {
					keys.add(fieldName);
				}
				if(fieldName.equals(incr)) {
					continue;
				}
				fields.add(fieldName);
			}
			pw.print("########");
			pw.print(name);
			pw.println("########");
			pw.println(wrapper("Insert", buildInsert(name, fields, filter)));
			pw.println(wrapper("Update", buildUpdate(name, fields, keys, filter)));
			pw.println(wrapper("Select", buildSelect(name, keys)));
			pw.println(wrapper("Delete", buildDelete(name, keys)));
			pw.println();
		}
	}



	public static String buildInsert(String name, List<String> fields, Filter filter) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		pw.println("insert into " + name + "(");
		boolean first = true;
		for(String field : fields) {
			if(filter!= null && filter.filterField("insert", field)) {
				continue;
			}
			if(!first) {
				pw.print(",");
			}else {
				pw.print("   ");
			}
			String columnName = toUnderscore(field);
			if(columnName.indexOf('_') > 0) {
				pw.print("  ");
			}else {
				pw.print("   ");
			}
			pw.print(columnName);
			first = false;
		}
		pw.println();
		pw.println(")values(");
		first = true;
		for(String field : fields) {
			if(filter!= null && filter.filterField("insert", field)) {
				continue;
			}
			if(!first) {
				pw.print(",");
			}else {
				pw.print("   ");
			}
			String val = null;
			if(filter!= null) {
				val = filter.getInsertFieldValue(field);
			}
			if(val == null) {
				val = String.format("#{%s}", field);
			}
			pw.print(val);
			first = false;
		}
		pw.println(")");
		pw.println();
		return sw.toString();
	}


	public static String buildUpdate(String name, List<String> fields, Set<String> keys, Filter filter) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		
		//pw.print("#");
		//pw.println(keys);
		pw.println("update " + name);
		pw.println("<trim prefix='set' suffixOverrides=','>");
		for(String field : fields) {
			if(keys.contains(field)) {
				continue;
			}
			if(filter!= null && filter.filterField("update", field)) {
				continue;
			}
			String var = null;
			if(filter!= null) {
				var = filter.getUpdateFieldValue(field);
				if(var != null) {
					pw.println(var);
					continue;
				}
			}
			var = String.format("#{%s}", field);
			pw.println(String.format("   <if test=' %s != null'>%s=%s</if>", field, toUnderscore(field), var));
		}
		pw.println("</trim>");
		boolean first = true;
		for(String key : keys) {
			if(first) {
				pw.print("where ");
			}else {
				pw.print("  and ");
			}
			pw.print(toUnderscore(key));
			pw.print("=");
			pw.println(String.format("#{%s}", key));
			first = false;
		}
		pw.println();
		return sw.toString();
	}

	public static String buildSelect(String name, Set<String> keys) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);

		pw.print("select * from " + name);
		boolean first = true;
		for(String key : keys) {
			if(first) {
				pw.print(" where ");
			}else {
				pw.print(" and ");
			}
			pw.print(toUnderscore(key));
			pw.print("=");
			pw.print(String.format("#{%s}", key));
			first = false;
		}
		pw.println();
		
		return sw.toString();
	}

	public static String buildDelete(String name, Set<String> keys) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);

		pw.print("delete from " + name);
		boolean first = true;
		for(String key : keys) {
			if(first) {
				pw.print(" where ");
			}else {
				pw.print(" and ");
			}
			pw.print(toUnderscore(key));
			pw.print("=");
			pw.print(String.format("#{%s}", key));
			first = false;
		}
		pw.println();
		
		return sw.toString();
	}


	public static String wrapper(String prefix, String str) throws Exception{
		LineNumberReader reader = new LineNumberReader(new StringReader(str));
		StringBuffer sb = new StringBuffer();
		sb.append("@").append(prefix).append("({\n");
		if("Update".equals(prefix)) {
			sb.append("\"<script>\",").append("\n");
		}
		while(true) {
			String line = reader.readLine();
			if(line == null) {
				break;
			}
			if("".equals(line)) {
				continue;
			}
			sb.append("\"").append(line).append("\",").append("\n");
		}
		if("Update".equals(prefix)) {
			sb.append("\"</script>\"");
		}else {
			sb.setLength(sb.length() - 2);
			
		}
		sb.append("\n").append("})");
		return sb.toString();
	}
}
