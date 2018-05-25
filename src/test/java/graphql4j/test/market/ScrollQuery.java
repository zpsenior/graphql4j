package graphql4j.test.market;


import java.util.Date;

public abstract class ScrollQuery extends PO {
	private int pageSize = 10;
	private long minvalue;
	private Date mindate;

	public final int getPageSize() {
		return pageSize;
	}

	public final void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public final long getMinvalue() {
		return minvalue;
	}

	public final void setMinvalue(long minvalue) {
		this.minvalue = minvalue;
	}

	public Date getMindate() {
		return mindate;
	}

	public void setMindate(Date mindate) {
		this.mindate = mindate;
	}
}
