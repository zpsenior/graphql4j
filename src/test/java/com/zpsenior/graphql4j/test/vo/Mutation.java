package com.zpsenior.graphql4j.test.vo;

import com.zpsenior.graphql4j.annotation.Field;
import com.zpsenior.graphql4j.annotation.Type;
import com.zpsenior.graphql4j.annotation.Var;

@Type("Mutation")
public class Mutation {
	
	@Field
	public Result addCategory(@Var("tenantId") String tenantIdId, @Var("name") String name) {
		return null;
	}

	@Field
	public void addGoods(@Var("tenantIdId") String tenantIdId, @Var("name") String name, @Var("categoryId") String categoryId) {
		return ;
	}

	@Field
	public void updateGoods(@Var("tenantIdId") String tenantIdId, @Var("goodsId") String goodsId, @Var("categoryId") String categoryId, @Var("name") String name) {
		return ;
	}
	
}
