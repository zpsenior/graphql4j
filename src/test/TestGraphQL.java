

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;

import graphql4j.operation.GraphQL;
import graphql4j.operation.GraphQLBuilder;
import graphql4j.operation.JSONParser;
import graphql4j.operation.JSONReader;
import graphql4j.engine.GraphQLExecute;
import graphql4j.type.GraphQLSchema;
import graphql4j.type.GraphQLSchemaLoader;
import graphql4j.type.ArrayType;

public class TestGraphQL {
	
	private static String query = "query("
	+"$users:[[Int]]=[[234,236],[290,297],[278,1,0]],"
	+"$param:ScrollQueryGoods={kind:'1',subkind:'2',status:'4',pageSize:10},"
	+"$name:String,$time:Date,$count:Int,"
	+"$orderParams:ScrollQueryGoodsOrder"
	+"$ver:[[Version]]"
	+")"
	+"{goods(goodsseq:1){goodsseq,goodsname,amount,...test, ... on TPGoods{createtime}}"
	//+"goodses:goodsList(params:$param){goodsseq,goodsname,amount,...test}"
	//+"userByNickname(uuid:'234',nickname:'wahaha')"
	+"goodsReview(count:$count,name:$name,time:$time)"
	+"goodsReviews(params:$orderParams)"
	+"orders(params:$orderParams)"
	+"status"
	+"}"
	+"fragment test on TPGoods{count,postfee,status,ownerseq,ctime:createtime}";
	
	private static String json = "{name:'goods',type:1,body:{itemseq:23445,path:'wellcode',items:[[234,567],[123],[895,34, 0,45]]}}";
	
	
	public static List<List<List<List<Object>>>> getListObj(){
		return null;
	}
	
	public static Map<String, List<List<List<Object>>>> getMapObj(){
		return null;
	}
	
	public static Map getMapObj1(){
		return null;
	}

	public static void main(String[] args) {
		try {
			test();
			//testJson();
			//testReflect();
			System.out.println(":" + (1 | 2));
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}
	
	public static void testReflect() throws Exception{
		Class<?> cls = TestGraphQL.class;
		for(Method m : cls.getDeclaredMethods()){
			Class<?> returnClass = m.getReturnType();
			Type returnType = m.getGenericReturnType();
			String name = m.getName();
			name = name.substring(3);
			name = name.substring(0, 1).toLowerCase() + name.substring(1);
			System.out.println("method: " + m.getName() + " " + name);
			System.out.println("class: " + returnClass.getName());
			System.out.println("type: " + returnType);
			if("java.util.List".equals(returnClass.getName())){
				System.out.println("arrayType: " + ArrayType.buildNestedArrayType(returnClass, returnType, null, false));
			}
			System.out.println();
		}
	}
	
	public static void test() throws Exception{
		GraphQL gql;
		GraphQLBuilder builder;
		GraphQLSchema schema;
		GraphQLSchemaLoader loader = new GraphQLSchemaLoader();
		schema = loader.load(new String[]{"com.chain.graphql.test.BOMarketQuery"});
		System.out.println(schema);
		
		builder = new GraphQLBuilder(schema);
		System.out.println(builder);
		gql = builder.build(query);
		System.out.println(gql);
		
		GraphQL.Operation opt = gql.getDefaultOperation();
		
		Object rootQuery = new BOMarketQuery();
		
		GraphQLExecute exec = new GraphQLExecute(schema);
		
		opt.bindValue("name", "hello,zp");
		opt.bindValue("time", System.currentTimeMillis());
		opt.bindValue("count", 1977);
		opt.bindValue("orderParams", "{ownerseq:12456,buyerseq:1036,keyword:'zhou.p',status:3,pageSize:34}");
		opt.bindValue("ver", "[[{majorVersion:11},{majorVersion:12}],[{majorVersion:21},{majorVersion:22}]]");
		
		String res = exec.query(opt, rootQuery);
		
		System.out.println(res);
	}
	
	public static void testJson() throws Exception{
		JSONReader reader = new JSONReader(new JSONParser(" [[{majorVersion:11},{majorVersion:12}],[{majorVersion:21},{majorVersion:22}]]"));
		Object obj = reader.readObject();
		
		System.out.println(obj);
	}

}
