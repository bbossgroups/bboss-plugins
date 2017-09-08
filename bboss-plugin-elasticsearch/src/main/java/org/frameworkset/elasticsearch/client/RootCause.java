package org.frameworkset.elasticsearch.client;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RootCause {
	private String type;//": "query_shard_exception",
	private String reason;//": "No mapping found for [start_time] in order to sort on",
	@JsonProperty("index_uuid")
	private String indexUuid;//": "Yh1s0aoDTdqa3ojpbYq2BQ",
	private String index;//": "trace-2017.08.31"
	
	private int line;
	private int col;
	public RootCause() {
		// TODO Auto-generated constructor stub
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public String getIndexUuid() {
		return indexUuid;
	}
	public void setIndexUuid(String indexUuid) {
		this.indexUuid = indexUuid;
	}
	public String getIndex() {
		return index;
	}
	public void setIndex(String index) {
		this.index = index;
	}
	public int getLine() {
		return line;
	}
	public void setLine(int line) {
		this.line = line;
	}
	public int getCol() {
		return col;
	}
	public void setCol(int col) {
		this.col = col;
	}

}
