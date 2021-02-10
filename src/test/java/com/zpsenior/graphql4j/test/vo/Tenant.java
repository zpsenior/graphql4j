package com.zpsenior.graphql4j.test.vo;

import java.util.Date;
import java.util.List;

import com.zpsenior.graphql4j.annotation.Field;
import com.zpsenior.graphql4j.annotation.Join;
import com.zpsenior.graphql4j.annotation.Type;

@Type("Tenant")
public class Tenant {

	@Field
	private String tenantId;

	@Field
	private String tenantName;

	@Field
	private String license;

	@Field
	private String address;

	@Field
	private Date createDate;

	@Field
	private Date approvalDate;

	@Join(bind = "queryAdminList", params = { "tenantId" }, map = { "managers" })
	private List<TenantAdmin> admins;

	@Join(bind = "queryUserList", params = { "tenantId" })
	private List<User> users;
}
