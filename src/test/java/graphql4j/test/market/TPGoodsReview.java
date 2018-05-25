package graphql4j.test.market;


import graphql4j.annotation.GraphQLObject;

import java.util.Date;

@GraphQLObject(valueObject=true)
public class TPGoodsReview extends PO {
	private long orderseq;
	private long goodsseq;
	private long buyerseq;
	private long ownerseq;
	private String imgs;
	private String goodsreview;
	private int goodsstarlevel;
	private Date time;
	private String status;
	public long getOrderseq() {
		return orderseq;
	}
	public void setOrderseq(long orderseq) {
		this.orderseq = orderseq;
	}
	public long getGoodsseq() {
		return goodsseq;
	}
	public void setGoodsseq(long goodsseq) {
		this.goodsseq = goodsseq;
	}
	public long getBuyerseq() {
		return buyerseq;
	}
	public void setBuyerseq(long buyerseq) {
		this.buyerseq = buyerseq;
	}
	public long getOwnerseq() {
		return ownerseq;
	}
	public void setOwnerseq(long ownerseq) {
		this.ownerseq = ownerseq;
	}
	public String getGoodsreview() {
		return goodsreview;
	}
	public void setGoodsreview(String goodsreview) {
		this.goodsreview = goodsreview;
	}
	public int getGoodsstarlevel() {
		return goodsstarlevel;
	}
	public void setGoodsstarlevel(int goodsstarlevel) {
		this.goodsstarlevel = goodsstarlevel;
	}
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}

	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getImgs() {
		return imgs;
	}
	public void setImgs(String imgs) {
		this.imgs = imgs;
	}

}
