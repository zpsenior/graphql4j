package com.zpsenior.graphql4j.test.vo;

import java.util.List;

import com.zpsenior.graphql4j.annotation.Field;
import com.zpsenior.graphql4j.annotation.Type;
import com.zpsenior.graphql4j.annotation.Variable;

@Type("Query")
public class Query {

	@Field
	public List<GoodsCategory> queryGoodsCategoryList(@Variable("tenantId") String tenantId){
		return null;
	}

	@Field
	public List<Goods> queryGoodsList(@Variable("tenantId") String tenantId, @Variable("categoryId") String categoryId){
		return null;
	}

	@Field
	public Goods getGoods(@Variable("tenantId") String tenantId, @Variable("goodsId") String goodsId){
		return null;
	}

	@Field
	public List<User> queryUserList(@Variable("tenantId") String tenantId){
		return null;
	}

	@Field
	public List<Admin> queryAdminList(@Variable("tenantId") String tenantId){
		return null;
	}

	@Field
	public List<Order> queryOrderListByAdmin(@Variable("tenantId") String tenantId){
		return null;
	}

	@Field
	public List<Order> queryOrderListByUser(@Variable("tenantId") String tenantId){
		return null;
	}

	@Field
	public Admin getAdmin(@Variable("tenantId") String tenantId, @Variable("adminId") String adminId){
		return null;
	}

	@Field
	public User getUser(@Variable("tenantId") String tenantId, @Variable("userId") String userId){
		return null;
	}

	@Field
	public Tenant getTenant(@Variable("tenantId") String tenantId){
		return null;
	}
}
