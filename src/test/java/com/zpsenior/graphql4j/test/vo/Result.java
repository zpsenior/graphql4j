package com.zpsenior.graphql4j.test.vo;

import com.zpsenior.graphql4j.annotation.Field;
import com.zpsenior.graphql4j.annotation.Type;

@Type("Result")
public class Result {

	@Field
	private String code;

	@Field
	private String msg;

	@Field
	private String data;
}
