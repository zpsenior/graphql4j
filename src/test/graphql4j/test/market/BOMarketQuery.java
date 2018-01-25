package graphql4j.test.market;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import graphql4j.annotation.GraphQLArgument;
import graphql4j.annotation.GraphQLField;
import graphql4j.annotation.GraphQLObject;

@GraphQLObject("Query")
public class BOMarketQuery {
	
	@GraphQLField("vip")
	public VIP getVIP(@GraphQLArgument("userseq") long userseq) throws Exception {
		return getVIPUser(userseq);
	}

	@GraphQLField("vipuser")
	public VIPUser getVIPUser(@GraphQLArgument("userseq") long userseq) throws Exception {
		VIPUser user = new VIPUser();
		System.out.println("userseq: " + userseq);
		user.setVipno("sliver2346");
		user.setUsername("VIP01");
		user.setMobileno("13899998888");
		return user;
	}

	@GraphQLField("user")
	public User getUser(@GraphQLArgument("username") String username, @GraphQLArgument("mobileno") String mobileno, @GraphQLArgument("nickname") String nickname) throws Exception {
		User user = new User();
		if(username != null){
			user.setUsername(username);
			System.out.println("username: " + username);
		}else if(mobileno != null){
			user.setNickname(nickname);
			System.out.println("mobileno: " + mobileno);
		}else if(nickname != null){
			user.setMobileno(mobileno);
			System.out.println("nickname: " + nickname);
		}else{
			throw new RuntimeException("no find param");
		}
		return user;
	}
	
	@GraphQLField("userList")
	public List<User> queryUserList(@GraphQLArgument("params") ScrollQueryUser params)throws Exception{
		System.out.println("query user params: " + params);
		List<User> list = new ArrayList<User>();
		for(int i = 0; i < params.getPageSize() * 2; i++){
			User user = new User();
			long userseq = System.currentTimeMillis() + Math.round(Math.random() * 100000);
			user.setUserseq(userseq);
			user.setUsername("user" + i);
			user.setCreatetime(new Date());
			list.add(user);
		}
		return list;
	}
	
	@GraphQLField("goodsList")
	public List<TPGoods> queryGoodsList(@GraphQLArgument("params") ScrollQueryGoods params)throws Exception{
		System.out.println("query goods params: " + params);
		List<TPGoods> list = new ArrayList<TPGoods>();
		for(int i = 0; i < params.getPageSize() * 2; i++){
			TPGoods goods = new TPGoods();
			long seq = System.currentTimeMillis() + Math.round(Math.random() * 100000);
			goods.setGoodsseq(seq);
			goods.setCreatetime(new Date());
			list.add(goods);
		}
		return list;
	}
	
	@GraphQLField("goods")
	public TPGoods getGoods(@GraphQLArgument("goodsseq") long goodsseq) throws Exception{
		System.out.println("query goods id: " + goodsseq);
		TPGoods goods = new TPGoods();
		goods.setGoodsseq(goodsseq);
		return goods;
	}
	
	@GraphQLField("goodsOrders")
	public List<TPGoodsOrder> queryMyGoodsOrderList(@GraphQLArgument("params") ScrollQueryGoodsOrder params) throws Exception{
		System.out.println("query goods order params: " + params);
		List<TPGoodsOrder> list = new ArrayList<TPGoodsOrder>();
		for(int i = 0; i < params.getPageSize() * 2; i++){
			TPGoodsOrder order = new TPGoodsOrder();
			list.add(order);
		}
		return list;
	}
	
	@GraphQLField("goodsOrder")
	public TPGoodsOrder getGoodsOrder(@GraphQLArgument("orderseq") long orderseq) throws Exception{
		System.out.println("query goods order id: " + orderseq);
		TPGoodsOrder order = new TPGoodsOrder();
		order.setOrderseq(orderseq);
		return order;
	}
	
	@GraphQLField("goodsReviews")
	public List<TPGoodsReview> queryGoodsReviewList(@GraphQLArgument("params") ScrollQueryGoodsOrder params)throws Exception{
		System.out.println("query goods review params:" + params);
		List<TPGoodsReview> list = new ArrayList<TPGoodsReview>();
		for(int i = 0; i < params.getPageSize() * 2; i++){
			TPGoodsReview review = new TPGoodsReview();
			list.add(review);
		}
		return list;
	}
}
