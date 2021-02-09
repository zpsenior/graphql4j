package com.zpsenior.graphql4j.test.vo;

import java.util.Date;

import com.zpsenior.graphql4j.annotation.Field;
import com.zpsenior.graphql4j.annotation.Type;

@Type("Admin")
public class Admin {


	@Field
	private String id;

	@Field
	private String name;

	@Field
	private String mobile;

	@Field
	private String status;

	@Field
	private Date createDate;
}
