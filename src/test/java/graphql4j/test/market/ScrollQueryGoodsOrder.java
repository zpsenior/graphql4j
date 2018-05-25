package graphql4j.test.market;

import graphql4j.annotation.GraphQLInput;

@GraphQLInput
public class ScrollQueryGoodsOrder extends ScrollQuery {

	private long ownerseq;
	private long buyerseq;
	private String keyword;
	private String status;


	public long getOwnerseq() {
		return ownerseq;
	}
	public void setOwnerseq(long ownerseq) {
		this.ownerseq = ownerseq;
	}
	public long getBuyerseq() {
		return buyerseq;
	}
	public void setBuyerseq(long buyerseq) {
		this.buyerseq = buyerseq;
	}
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
}
