package com.zpsenior.graphql4j.test.vo;

import com.zpsenior.graphql4j.annotation.Field;
import com.zpsenior.graphql4j.annotation.Type;
import com.zpsenior.graphql4j.annotation.Variable;

@Type("Mutation")
public class Mutation {
	
	@Field
	public Result addCategory(@Variable("tenantId") String tenantIdId, @Variable("name") String name) {
		return null;
	}

	@Field
	public void addGoods(@Variable("tenantIdId") String tenantIdId, @Variable("name") String name, @Variable("categoryId") String categoryId) {
		return ;
	}

	@Field
	public void updateGoods(@Variable("tenantIdId") String tenantIdId, @Variable("goodsId") String goodsId, @Variable("categoryId") String categoryId, @Variable("name") String name) {
		return ;
	}
	
}
