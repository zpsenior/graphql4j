package graphql4j.test;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import graphql4j.operation.GraphQL;
import graphql4j.operation.GraphQLBuilder;
import graphql4j.engine.GraphQLExecute;
import graphql4j.test.market.BOMarketQuery;
import graphql4j.type.GraphQLSchema;
import graphql4j.type.GraphQLSchemaLoader;

public class TestGraphQL {
	
	private static String json = "{name:'goods',type:1,body:{itemseq:23445,path:'wellcode',items:[[234,567],[123],[895,34, 0,45]]}}";

	public static void main(String[] args) {
		try {
			testGQL(readResource("graphql4j/test/market.gql"));
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}
	
	public static String readResource(String resource)throws Exception{
		InputStream is = TestGraphQL.class.getClassLoader().getResourceAsStream(resource);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] buff = new byte[1024];
		while(true){
			int cnt = is.read(buff);
			if(cnt < 0){
				break;
			}
			bos.write(buff, 0, cnt);
		}
		return bos.toString();
	}

	
	public static void testGQL(String query) throws Exception{
		GraphQL gql;
		GraphQLBuilder builder;
		GraphQLSchema schema;
		GraphQLSchemaLoader loader = new GraphQLSchemaLoader();
		schema = loader.load(new String[]{"graphql4j.test.market.BOMarketQuery"});
		//System.out.println(schema);
		
		builder = new GraphQLBuilder(schema);
		gql = builder.build(query);
		System.out.println(gql);
		
		GraphQL.Operation opt = gql.getDefaultOperation();
		
		Object rootQuery = new BOMarketQuery();
		
		GraphQLExecute exec = new GraphQLExecute(schema);
		
		opt.bindValue("orderParams", "{ownerseq:12456,buyerseq:1036,keyword:'zhou.p',status:3,pageSize:34}");
		
		String res = exec.query(opt, rootQuery);
		
		System.out.println(res);
	}


}