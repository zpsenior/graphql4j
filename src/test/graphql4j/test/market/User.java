package graphql4j.test.market;


import graphql4j.annotation.GraphQLObject;

import java.io.Serializable;
import java.util.Date;

@GraphQLObject(valueObject=true)
public class User extends PO implements Serializable {
	
	private static final long serialVersionUID = -963847391051988270L;

	public final static String SYS_UUID = "53733437cfa24ac195ea94799edcda7c";
	
	public final static String STATUS_OK     = "0";
	public final static String STATUS_SHIELD = "2";

	private long   userseq;
	private String username;
	private String nationcode;
	private String mobileno;
	private String nickname;
	
	private String platform;
	private String device;
	
	private String password;

	private String gender;
	private String residence;
	private String photopath;
	
	private String status;
	
	private Date createtime;
	private Date lastlogin;
	public long getUserseq() {
		return userseq;
	}
	public void setUserseq(long userseq) {
		this.userseq = userseq;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getNationcode() {
		return nationcode;
	}

	public void setNationcode(String nationcode) {
		this.nationcode = nationcode;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getPlatform() {
		return platform;
	}
	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public String getDevice() {
		return device;
	}

	public void setDevice(String device) {
		this.device = device;
	}

	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getMobileno() {
		return mobileno;
	}
	public void setMobileno(String mobileno) {
		this.mobileno = mobileno;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getPhotopath() {
		return photopath;
	}
	public void setPhotopath(String photopath) {
		this.photopath = photopath;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getResidence() {
		return residence;
	}
	public void setResidence(String residence) {
		this.residence = residence;
	}
	public Date getCreatetime() {
		return createtime;
	}
	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	public Date getLastlogin() {
		return lastlogin;
	}
	public void setLastlogin(Date lastlogin) {
		this.lastlogin = lastlogin;
	}


	
	
}
