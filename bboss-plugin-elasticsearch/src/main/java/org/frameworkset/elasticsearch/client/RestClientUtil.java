package org.frameworkset.elasticsearch.client;

import org.elasticsearch.client.Client;
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
	public   void addEvent(Event event,ElasticSearchEventSerializer elasticSearchEventSerializer) throws Exception {
	    if (bulkBuilder == null) {
	    	 bulkBuilder = new StringBuilder();
	    }
	    client.createIndexRequest(bulkBuilder, indexNameBuilder, event,  elasticSearchEventSerializer);
	     
	  }

	@Override
	public void updateIndexs(Event event, ElasticSearchEventSerializer elasticSearchEventSerializer) throws Exception {

	}

	 /**
	  * 
	  * @param path
	  * @param string
	  * @return
	  */
	 public Object executeRequest(String path) throws Exception 
	 {
		 return executeRequest( path,null);
	 }
	public Object execute() throws Exception {
		  return client.execute(this.bulkBuilder.toString());
	  }

	@Override
	public Client getClient() {
		return null;
	}

	@Override
	public void deleteIndex(String indexName, String indexType, String... ids) throws Exception {
		// TODO Auto-generated method stub
		
	}
	@Override
	public String executeRequest(String path, String entity) throws Exception {
		// TODO Auto-generated method stub
		return this.client.executeRequest(path,entity);
	}
	@Override
	public String delete(String path, String string) {
		// TODO Auto-generated method stub
		return null;
	}

}
