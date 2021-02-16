package com.zpsenior.graphql4j.ql;

public abstract class QLNode {
	
	public abstract void toString(int deep, StringBuffer sb);
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		toString(0, sb);
		return sb.toString();
	}

}
