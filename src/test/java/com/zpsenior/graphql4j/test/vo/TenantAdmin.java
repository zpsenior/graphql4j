package com.zpsenior.graphql4j.test.vo;

import java.util.Date;

import com.zpsenior.graphql4j.annotation.Field;
import com.zpsenior.graphql4j.annotation.Type;

@Type("TenantAdmin")
public class TenantAdmin {

	@Field
	private String tenantId;

	@Field
	private String adminId;

	@Field
	private String openId;

	@Field
	private String adminName;

	@Field
	private String mobile;

	@Field
	private UserStatus status;

	@Field
	private Date createDate;
}
