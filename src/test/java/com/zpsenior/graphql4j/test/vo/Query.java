package com.zpsenior.graphql4j.test.vo;

import java.util.List;

import com.zpsenior.graphql4j.annotation.Field;
import com.zpsenior.graphql4j.annotation.Type;
import com.zpsenior.graphql4j.annotation.Var;

@Type("Query")
public class Query {

	@Field
	public List<GoodsCategory> queryGoodsCategoryList(@Var("tenantId") String tenantId){
		return null;
	}

	@Field
	public List<Goods> queryGoodsList(@Var("tenantId") String tenantId, @Var("categoryId") String categoryId){
		return null;
	}

	@Field
	public Goods getGoods(@Var("tenantId") String tenantId, @Var("goodsId") String goodsId){
		return null;
	}

	@Field
	public List<User> queryUserList(@Var("tenantId") String tenantId){
		return null;
	}

	@Field
	public List<Admin> queryAdminList(@Var("tenantId") String tenantId){
		return null;
	}

	@Field
	public List<Order> queryOrderListByAdmin(@Var("tenantId") String tenantId){
		return null;
	}

	@Field
	public List<Order> queryOrderListByUser(@Var("tenantId") String tenantId){
		return null;
	}

	@Field
	public Admin getAdmin(@Var("tenantId") String tenantId, @Var("adminId") String adminId){
		return null;
	}

	@Field
	public User getUser(@Var("tenantId") String tenantId, @Var("userId") String userId){
		return null;
	}

	@Field
	public Tenant getTenant(@Var("tenantId") String tenantId){
		return null;
	}
}
