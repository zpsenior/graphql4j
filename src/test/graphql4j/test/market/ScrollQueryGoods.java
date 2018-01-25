package graphql4j.test.market;

import graphql4j.annotation.GraphQLInput;


@GraphQLInput
public class ScrollQueryGoods extends ScrollQuery {
	
	private String kind;
	private String subkind;
	private String keyword;
	private String status;
	private Long owner;
	
	public String getKind() {
		return kind;
	}
	public void setKind(String kind) {
		this.kind = kind;
	}
	public String getSubkind() {
		return subkind;
	}
	public void setSubkind(String subkind) {
		this.subkind = subkind;
	}
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public Long getOwner() {
		return owner;
	}
	public void setOwner(Long owner) {
		this.owner = owner;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

}
