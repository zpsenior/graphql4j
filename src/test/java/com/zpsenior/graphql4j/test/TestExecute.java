package com.zpsenior.graphql4j.test;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.zpsenior.graphql4j.ql.QLBuilder;
import com.zpsenior.graphql4j.ql.QLContext;
import com.zpsenior.graphql4j.ql.QLRoot;
import com.zpsenior.graphql4j.schema.Schema;
import com.zpsenior.graphql4j.test.input.QueryGoodsParam;
import com.zpsenior.graphql4j.test.vo.Mutation;
import com.zpsenior.graphql4j.test.vo.Query;
import com.zpsenior.graphql4j.utils.ClassFinder;

public class TestExecute {

	public static void main(String[] args) {
		FileReader fr;
		try {
			fr = new FileReader(args[0]);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			return;
		}
		
		try {
			doLoop(fr);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}finally {
			try {
				fr.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static void doLoop(FileReader fr) throws Exception{

		Schema schema;
		
		schema = new Schema(Query.class, Mutation.class);
		schema.printSchema();
		
		QLRoot root = new QLRoot();
		QLBuilder builder = new QLBuilder();
		ClassFinder finder = new ClassFinder(QueryGoodsParam.class.getPackage().getName());

		builder.build(fr, finder, root);
		
		root.bind(schema);
		
		//QLContext context = new QLContext(finder, joiner);
		
		//root.mutation(entryName, finder, joiner);
		//root.query(entryName, finder, joiner);
	}

}
