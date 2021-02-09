package com.zpsenior.graphql4j.test.vo;

import com.zpsenior.graphql4j.annotation.Field;
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
	private String categoryId;
	
	@Field
	private GoodsCategory category;
	
}
