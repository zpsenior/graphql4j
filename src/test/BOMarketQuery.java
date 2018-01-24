

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import graphql4j.annotation.GraphQLArgument;
import graphql4j.annotation.GraphQLField;
import graphql4j.annotation.GraphQLObject;

@GraphQLObject("Query")
public class BOMarketQuery {

	@GraphQLField("userByName")
	public User getUserByName(@GraphQLArgument("username") String username) throws Exception {
		return null;
	}
	
	@GraphQLField("userByNickname")
	public User getUserByNickname(@GraphQLArgument("uuid") String uuid, @GraphQLArgument("nickname") String nickname) throws Exception {
		System.out.println("uuid: " + uuid);
		System.out.println("nickname: " + nickname);
		User user =new User();
		user.setNickname("hellowrold");
		user.setUuid("dgdsykkuilolop");
		return user;
	}
	
	@GraphQLField("goodsList")
	public List<TPGoods> queryGoodsList(@GraphQLArgument("params") ScrollQueryGoods params)throws Exception{
		List<TPGoods> list = new ArrayList<TPGoods>();
		for(int i = 0; i < 10; i++){
			TPGoods goods = new TPGoods();
			long time = System.currentTimeMillis() + Math.round(Math.random() * 100000);
			goods.setCreatetime(new Date(time));
			list.add(goods);
		}
		return list;
	}
	
	@GraphQLField("goods")
	public TPGoods getGoods(@GraphQLArgument("goodsseq") int goodsseq) throws Exception{
		return null;
	}
	
	@GraphQLField("goodsOrders")
	public List<TPGoodsOrder> queryMyGoodsOrderList(@GraphQLArgument("params") ScrollQueryGoodsOrder params) throws Exception{
		return null;
	}
	
	@GraphQLField("goodsOrder")
	public TPGoodsOrder getGoodsOrder(@GraphQLArgument("orderseq") int orderseq) throws Exception{
		return null;
	}
	
	@GraphQLField("goodsReviews")
	public List<TPGoodsReview> queryGoodsReviewList(@GraphQLArgument("params") ScrollQueryGoodsOrder params)throws Exception{
		System.out.println("params:" + params);
		return null;
	}
	
	@GraphQLField("goodsReview")
	public List<TPGoodsReview> getGoodsReview(@GraphQLArgument("name") String name, @GraphQLArgument("time")  Date time, @GraphQLArgument("count")  int count)throws Exception{
		System.out.println("name: " + name);
		System.out.println("time: " + time);
		System.out.println("count: " + count);
		return null;
	}
	

	
	@GraphQLField("orders")
	public List<List<List<List<Integer>>>> queryMyGoodsOrderListtest(@GraphQLArgument("params") ScrollQueryGoodsOrder params) throws Exception{
		List<List<List<List<Integer>>>> list1 = new ArrayList<List<List<List<Integer>>>>();
		List<List<List<Integer>>> list2 = new ArrayList<List<List<Integer>>>();
		List<List<Integer>> list3 = new ArrayList<List<Integer>>();
		List<Integer> list4 = new ArrayList<Integer>();
		list4.add(888);
		list4.add(666);
		list4.add(777);
		list3.add(list4);
		list3.add(list4);
		list2.add(list3);
		list2.add(list3);
		list1.add(list2);
		return list1;
	}
	
	@GraphQLField("status")
	public Test getStatus(){
		return Test.TEST1;
	}
}
