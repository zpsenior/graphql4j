package graphql4j.test.market;


import graphql4j.annotation.GraphQLObject;

import java.util.Date;

@GraphQLObject(valueObject=true)
public class TPGoodsOrder extends PO {
	
	private long orderseq;
	private long goodsseq;
	private String goodsname;
	private String descript;
	private String coverpath;
	private int count;
	private int amount;
	private int postfee;
	private long ownerseq;
	private long buyerseq;
	private String paychannel;
	private String payid;
	private String tradeno;
	private Date paytime;
	private String status;
	private String remark;
	private String recipients;
	private String residence;
	private String addr;
	private String mobileno;
	private String postorder;
	private String postcomp;
	private Date deliverytime;
	private Date createtime;
	private Date finishtime;
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
	public long getBuyerseq() {
		return buyerseq;
	}
	public void setBuyerseq(long buyerseq) {
		this.buyerseq = buyerseq;
	}
	public String getPaychannel() {
		return paychannel;
	}
	public void setPaychannel(String paychannel) {
		this.paychannel = paychannel;
	}
	public String getPayid() {
		return payid;
	}
	public void setPayid(String payid) {
		this.payid = payid;
	}
	public String getTradeno() {
		return tradeno;
	}
	public void setTradeno(String tradeno) {
		this.tradeno = tradeno;
	}
	public Date getPaytime() {
		return paytime;
	}
	public void setPaytime(Date paytime) {
		this.paytime = paytime;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getRecipients() {
		return recipients;
	}
	public void setRecipients(String recipients) {
		this.recipients = recipients;
	}
	public String getResidence() {
		return residence;
	}
	public void setResidence(String residence) {
		this.residence = residence;
	}
	public String getAddr() {
		return addr;
	}
	public void setAddr(String addr) {
		this.addr = addr;
	}
	public String getMobileno() {
		return mobileno;
	}
	public void setMobileno(String mobileno) {
		this.mobileno = mobileno;
	}
	public String getPostorder() {
		return postorder;
	}
	public void setPostorder(String postorder) {
		this.postorder = postorder;
	}
	public String getPostcomp() {
		return postcomp;
	}
	public void setPostcomp(String postcomp) {
		this.postcomp = postcomp;
	}
	public Date getDeliverytime() {
		return deliverytime;
	}
	public void setDeliverytime(Date deliverytime) {
		this.deliverytime = deliverytime;
	}
	public Date getCreatetime() {
		return createtime;
	}
	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	public Date getFinishtime() {
		return finishtime;
	}
	public void setFinishtime(Date finishtime) {
		this.finishtime = finishtime;
	}
	
}
