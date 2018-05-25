package graphql4j.test.market;


import graphql4j.annotation.GraphQLObject;

import java.util.Date;

@GraphQLObject(valueObject=true)
public class TPGoods extends PO {
	private long goodsseq;
	private String goodsname;
	private String descript;
	private String coverpath;
	private int count;
	private int amount;
	private int postfee;
	private long ownerseq;
	private String status;
	private Date createtime;
	
	public long getGoodsseq() {
		return goodsseq;
	}
	public void setGoodsseq(long goodsseq) {
		this.goodsseq = goodsseq;
	}
	public String getGoodsname() {
		return goodsname;
	}
	public void setGoodsname(String goodsname) {
		this.goodsname = goodsname;
	}
	public String getDescript() {
		return descript;
	}
	public void setDescript(String descript) {
		this.descript = descript;
	}
	public String getCoverpath() {
		return coverpath;
	}
	public String[] getFullcoverpath(){
		if(coverpath == null||(coverpath.isEmpty())){
			return null;
		}
		String[] photos = coverpath.split(",");
		for(int i = 0; i < photos.length; i++){
			String photo = photos[i];
			photo = getFullpath(photo);
			photos[i] = photo;
		}
		return photos;
	}
	public void setCoverpath(String coverpath) {
		this.coverpath = coverpath;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public int getAmount() {
		return amount;
	}
	public void setAmount(int amount) {
		this.amount = amount;
	}
	public int getPostfee() {
		return postfee;
	}
	public void setPostfee(int postfee) {
		this.postfee = postfee;
	}
	public long getOwnerseq() {
		return ownerseq;
	}
	public void setOwnerseq(long ownerseq) {
		this.ownerseq = ownerseq;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Date getCreatetime() {
		return createtime;
	}
	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	
}
