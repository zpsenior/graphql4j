package com.zpsenior.graphql4j.utils;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import com.zpsenior.graphql4j.annotation.Field;
import com.zpsenior.graphql4j.schema.Member;
import com.zpsenior.graphql4j.schema.Schema;
import com.zpsenior.graphql4j.schema.TypeConfig;

public class DBSchemaBuilder extends SQLBuilder{
	
	private Schema schema;
	
	public DBSchemaBuilder(Schema schema) {
		this.schema = schema;
	}

	public void build(PrintWriter pw, Filter filter) {
		for(String name : schema.getTypeNames()) {
			if(name.startsWith("Query") || name.startsWith("Mutation")) {
				continue;
			}
			TypeConfig tc = schema.getTypeConfig(name);
			String className = tc.getBindClass().getName();
			if(filter != null) {
				name = filter.mapTableName(name, className);
			}
			String desc = tc.getAnn().desc();
			String incr = SQLBuilder.toUnderscore(tc.getAnn().incr());
			pw.println("#" + desc);
			pw.println("drop table `" + name + "`;");
			pw.println("create table `" + name + "`(");
			List<String> keys = new ArrayList<>();
			for(Member member : tc.getMembers()) {
				if(member.isMethod()|| member.getJoin() != null) {
					continue;
				}
				String fieldName = SQLBuilder.toUnderscore(member.getName());
				Field fld = member.getField();
				if(fld != null && fld.isKey()) {
					keys.add(fieldName);
				}
				pw.print("   ");
				pw.print(String.format("%-17s", "`" + fieldName + "`"));
				pw.print("   ");
				String autoIncr = "";
				if(fieldName.equals(incr)) {
					autoIncr = " auto_increment";
				}
				pw.print(String.format("%-21s", filter.convertType(member.getValueTypeName(), fld.len()) + autoIncr));
				if(!"".equals(fld.desc())) {
					pw.print(" comment '");
					pw.print(fld.desc());
					pw.print("'");
				}
				pw.println(",");
			}
			pw.print("   ");
			if(!"".equals(incr)) {
				pw.print("primary key(`" + incr + "`)");
				if(keys.size() > 1) {
					pw.println(",");
					pw.print("   ");
					pw.println("unique  key(" + wrapperArray(keys) + ")");
				}else {
					pw.println();
				}
			}else{
				pw.println("primary key(" + wrapperArray(keys) + ")");
			}
			pw.print(") comment'");
			pw.print(desc);
			pw.println("';");
			if(!"".equals(incr)) {
				pw.println("alter table `" + name  + "` AUTO_INCREMENT=10000;");
			}
		}
	}
}
