package com.zpsenior.graphql4j.test.vo;

import com.zpsenior.graphql4j.annotation.Field;
import com.zpsenior.graphql4j.annotation.Join;
import com.zpsenior.graphql4j.annotation.Type;

@Type("Goods")
public class Goods {

	@Field
	private String tenantId;

	@Field
	private String goodsId;

	@Field
	private String goodsName;

	@Field
	private String description;

	@Field
	private String[] img;
	
	@Field
	private int price;
	
	@Field
	private int discount;

	@Field
	private String categoryId;
	
	@Join(request = "getGoodsCategory", params = { "tenantId", "categoryId" })
	private GoodsCategory category;

	@Field
	private int totalCount;

	@Field
	private int saleCount;

	@Field
	private int GoodsStatus;
	
}
