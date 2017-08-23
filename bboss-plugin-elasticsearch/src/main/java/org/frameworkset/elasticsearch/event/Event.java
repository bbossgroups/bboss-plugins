package org.frameworkset.elasticsearch.event;

import java.util.Map;

public interface Event<T> {

	  /**
	   * Returns a map of name-value pairs describing the data stored in the body.
	   */
	  public Map<String, String> getHeaders();

	  /**
	   * Set the event headers
	   * @param headers Map of headers to replace the current headers.
	   */
	  public void setHeaders(Map<String, String> headers);

	  /**
	   * Returns the raw byte array of the data contained in this event.
	   */
	  public T getBody();

	  /**
	   * Sets the raw byte array of the data contained in this event.
	   * @param body The data.
	   */
	  public void setBody(T body);
	  
	  public void setIndexType(String indexType);
	  public String getIndexType();
	  public void setTTL(long ttl);
	  public long getTTL();
	  public String getIndexPrefix();
	  public void setIndexPrefix(String indexPrefix);

}
