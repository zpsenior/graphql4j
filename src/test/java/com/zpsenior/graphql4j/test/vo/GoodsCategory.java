package com.zpsenior.graphql4j.test.vo;

import java.util.List;

import com.zpsenior.graphql4j.annotation.Field;
import com.zpsenior.graphql4j.annotation.Join;
import com.zpsenior.graphql4j.annotation.Type;

@Type("GoodsCategory")
public class GoodsCategory {

	@Field
	private String tenantId;

	@Field
	private String id;

	@Field
	private String name;


	@Join(bind = "queryGoodsList", params = { "tenantId", "categoryId" })
	private List<Goods> goodsList;

}
