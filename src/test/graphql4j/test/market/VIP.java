package graphql4j.test.market;

import graphql4j.annotation.GraphQLInterface;

@GraphQLInterface(valueObject=true)
public interface VIP {
	public String getVipno();
}
