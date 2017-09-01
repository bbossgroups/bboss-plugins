package org.frameworkset.elasticsearch.client;

public class Shards {
	private long total;
	private long successful;
	private long failed;
	public Shards() {
		// TODO Auto-generated constructor stub
	}
	public long getTotal() {
		return total;
	}
	public void setTotal(long total) {
		this.total = total;
	}
	public long getSuccessful() {
		return successful;
	}
	public void setSuccessful(long successful) {
		this.successful = successful;
	}
	public long getFailed() {
		return failed;
	}
	public void setFailed(long failed) {
		this.failed = failed;
	}

}
