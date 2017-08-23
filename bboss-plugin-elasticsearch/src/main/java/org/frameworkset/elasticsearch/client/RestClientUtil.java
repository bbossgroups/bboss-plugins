package org.frameworkset.elasticsearch.client;

import org.frameworkset.elasticsearch.ElasticSearchEventSerializer;
import org.frameworkset.elasticsearch.IndexNameBuilder;
import org.frameworkset.elasticsearch.event.Event;

public class RestClientUtil implements ClientUtil{
	private ElasticSearchRestClient client;
	private StringBuilder bulkBuilder;
	 private IndexNameBuilder indexNameBuilder;
	public RestClientUtil(ElasticSearchClient client,IndexNameBuilder indexNameBuilder) {
		this.client = (ElasticSearchRestClient)client;
		this.indexNameBuilder = indexNameBuilder;
	}
	public   void addEvent(Event<Object> event,ElasticSearchEventSerializer elasticSearchEventSerializer) throws Exception {
	    if (bulkBuilder == null) {
	    	 bulkBuilder = new StringBuilder();
	    }
	    client.createIndexRequest(bulkBuilder, indexNameBuilder, event,  elasticSearchEventSerializer);
	     
	  }

 
	  public void execute() throws Exception {
		  client.execute(this.bulkBuilder.toString());
	  }

}
