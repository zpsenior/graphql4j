package com.zpsenior.graphql4j.test;

import java.io.FileReader;

import com.zpsenior.graphql4j.input.InputFinder;
import com.zpsenior.graphql4j.ql.QLBuilder;
import com.zpsenior.graphql4j.ql.QLRoot;

public class TestQL {

	public static void main(String[] args) {
		try {
			initQL(args);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void initQL(String[] args)throws Exception {
		FileReader fr = new FileReader(args[0]);
		QLRoot root = new QLRoot();
		QLBuilder builder = new QLBuilder();
		InputFinder finder = new InputFinder() {

			@Override
			public Class<?> findClass(String name) throws Exception {
				return null;
			}
			
		};
		builder.build(fr, finder, root);
	}

}
