package org.frameworkset.elasticsearch.event;

import java.util.HashMap;
import java.util.Map;

public class ObjectEvent implements Event<Object> {

	private long TTL;
	private String indexType;
	private Map<String, String> headers;
	private Object body;
	private String indexPrefix;

	public ObjectEvent(Object body) {
		headers = new HashMap<String, String>();
		this.body = body;
	}

	@Override
	public Map<String, String> getHeaders() {
		return headers;
	}

	@Override
	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

	@Override
	public Object getBody() {
		return body;
	}

	@Override
	public void setBody(Object body) {

		this.body = body;
	}

	public long getTTL() {
		return TTL;
	}

	public void setTTL(long tTL) {
		TTL = tTL;
	}

	public String getIndexType() {
		return indexType;
	}

	public void setIndexType(String indexType) {
		this.indexType = indexType;
	}

	public String getIndexPrefix() {
		return indexPrefix;
	}

	public void setIndexPrefix(String indexPrefix) {
		this.indexPrefix = indexPrefix;
	}

}
