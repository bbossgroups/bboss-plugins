package org.frameworkset.elasticsearch.client;

import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.update.UpdateRequestBuilder;
import org.elasticsearch.client.Client;
import org.frameworkset.elasticsearch.ElasticSearchEventSerializer;
import org.frameworkset.elasticsearch.EventDeliveryException;
import org.frameworkset.elasticsearch.IndexNameBuilder;
import org.frameworkset.elasticsearch.event.Event;

public class TransportClientUtil  implements ClientUtil{
	private ElasticSearchTransportClient client;
	 private BulkRequestBuilder bulkRequestBuilder;
	 private IndexNameBuilder indexNameBuilder;
	public TransportClientUtil(ElasticSearchClient client,IndexNameBuilder indexNameBuilder) {
		this.client = (ElasticSearchTransportClient)client;
		this.indexNameBuilder = indexNameBuilder;
	}
	public void addEvent(Event event,ElasticSearchEventSerializer  elasticSearchEventSerializer) throws Exception {
		init();

	    
	    IndexRequestBuilder indexRequestBuilder = client.createIndexRequest(
		           indexNameBuilder , event,  elasticSearchEventSerializer);
//	    if (indexRequestBuilderFactory == null) {
//	      XContentBuilder bytesStream = null;
//	      try {
//	        bytesStream = client.getContentBuilder(event);
//	        indexRequestBuilder = client
//	                .prepareIndex(indexNameBuilder.getIndexName(event), indexType)
//	                .setSource(bytesStream );
//	      }
//	      finally {
//	        if(bytesStream != null){
////	          bytesStream.cl
//	        }
//	      }
//
//	    } else {
//	      indexRequestBuilder = client.createIndexRequest(
//	           indexNameBuilder.getIndexPrefix(event), indexType, event);
//	    }
//
//	    if (ttlMs > 0) {
//	      indexRequestBuilder.setTTL(ttlMs);
//	    }
	    bulkRequestBuilder.add(indexRequestBuilder);
	  }

 
	  public Object execute() throws Exception {
	    try {
	      BulkResponse bulkResponse = bulkRequestBuilder.execute().actionGet();
	      if (bulkResponse.hasFailures()) {
	        throw new EventDeliveryException(bulkResponse.buildFailureMessage());
	      }
	      return bulkResponse;
	    } finally {
	      
	    }
	  }
	  private void init(){
		  if (bulkRequestBuilder == null) {
		      bulkRequestBuilder = client.prepareBulk();
		    }
	  }
	 
	
	@Override
	public void deleteIndex(String indexName, String indexType, String... ids) throws Exception {
		init();
		for(int i = 0; i < ids.length; i ++){
			bulkRequestBuilder.add(client.deleteIndex(  indexName,   indexType,   ids[i]));
		}
		
	}
	public void updateIndexs(Event event,ElasticSearchEventSerializer  elasticSearchEventSerializer)throws Exception{
		UpdateRequestBuilder indexRequestBuilder = client.updateIndexRequest(
				 event,  elasticSearchEventSerializer);
//	    if (indexRequestBuilderFactory == null) {
//	      XContentBuilder bytesStream = null;
//	      try {
//	        bytesStream = client.getContentBuilder(event);
//	        indexRequestBuilder = client
//	                .prepareIndex(indexNameBuilder.getIndexName(event), indexType)
//	                .setSource(bytesStream );
//	      }
//	      finally {
//	        if(bytesStream != null){
////	          bytesStream.cl
//	        }
//	      }
//
//	    } else {
//	      indexRequestBuilder = client.createIndexRequest(
//	           indexNameBuilder.getIndexPrefix(event), indexType, event);
//	    }
//
//	    if (ttlMs > 0) {
//	      indexRequestBuilder.setTTL(ttlMs);
//	    }
		bulkRequestBuilder.add(indexRequestBuilder);
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
	@Override
	public Client getClient() {
		// TODO Auto-generated method stub
		return this.client.getClient();
	}
	@Override
	public String executeRequest(String path, String string) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String delete(String path, String string) {
		// TODO Auto-generated method stub
		return null;
	}
	

}
