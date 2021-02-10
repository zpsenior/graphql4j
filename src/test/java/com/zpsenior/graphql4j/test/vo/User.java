package com.zpsenior.graphql4j.test.vo;

import java.util.Date;

import com.zpsenior.graphql4j.annotation.Field;
import com.zpsenior.graphql4j.annotation.Type;

@Type("User")
public class User {

	@Field
	private String tenantId;

	@Field
	private String userId;

	@Field
	private String openId;

	@Field
	private String userName;
	
	@Field
	private Gender gender;
	
	@Field
	private String mobile;

	@Field
	private Date createDate;

	@Field
	private UserStatus status;
}
