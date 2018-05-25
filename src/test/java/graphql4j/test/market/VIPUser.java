package graphql4j.test.market;

import graphql4j.annotation.GraphQLObject;

@GraphQLObject(valueObject=true)
public class VIPUser extends User implements VIP {

	private static final long serialVersionUID = -3424460650642875339L;
	
	private String vipno;

	public String getVipno() {
		return vipno;
	}

	public void setVipno(String vipno) {
		this.vipno = vipno;
	}
	
	

}
