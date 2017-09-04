package org.frameworkset.elasticsearch.client;

import java.io.IOException;
import java.util.Map;

import org.apache.http.client.ResponseHandler;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.update.UpdateRequestBuilder;
import org.elasticsearch.client.Client;
import org.frameworkset.elasticsearch.ElasticSearchEventSerializer;
import org.frameworkset.elasticsearch.ElasticSearchException;
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
	public void addEvent(Event event,ElasticSearchEventSerializer  elasticSearchEventSerializer) throws ElasticSearchException {
		init();

	    
	    try {
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
		} catch (IOException e) {
			throw new ElasticSearchException(e);
		}
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
	public void deleteIndex(String indexName, String indexType, String... ids) throws ElasticSearchException {
		init();
		for(int i = 0; i < ids.length; i ++){
			try {
				bulkRequestBuilder.add(client.deleteIndex(  indexName,   indexType,   ids[i]));
			} catch (Exception e) {
				throw new ElasticSearchException(e);
			}
		}
		
	}
	public void updateIndexs(Event event,ElasticSearchEventSerializer  elasticSearchEventSerializer)throws ElasticSearchException{
		try {
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
		} catch (IOException e) {
			throw new ElasticSearchException(e);
		}
	}
	/**
	  * 
	  * @param path
	  * @return
	  */
	 public Object executeRequest(String path) throws ElasticSearchException 
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
	@Override
	public String executeRequest(String path, String templateName, Map params) throws ElasticSearchException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String executeRequest(String path, String templateName, Object params) throws ElasticSearchException {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	@Override
	public String executeHttp(String path, String action) throws ElasticSearchException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String executeHttp(String path, String entity,String action) throws ElasticSearchException {
		// TODO Auto-generated method stub
		return null;
	}
	
	public String getIndexMapping(String index) throws ElasticSearchException{
		return null;
	}
	
	public <T> T executeRequest(String path, String templateName,Map params,ResponseHandler<T> responseHandler) throws ElasticSearchException{
		return null;
	}
	
	 
	public <T> T  executeRequest(String path, String templateName,Object params,ResponseHandler<T> responseHandler) throws ElasticSearchException{
		return null;
	}
	@Override
	public <T> T executeRequest(String path, String entity, ResponseHandler<T> responseHandler)
			throws ElasticSearchException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public SearchResult search(String path, String templateName, Map params) throws ElasticSearchException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public SearchResult search(String path, String templateName, Object params) throws ElasticSearchException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public SearchResult search(String path, String entity) throws ElasticSearchException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String dropIndice(String index) throws ElasticSearchException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String updateIndiceMapping(String action, String indexMapping) throws ElasticSearchException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String createIndiceMapping(String indexName, String indexMapping) throws ElasticSearchException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String updateIndiceMapping(String action, String templateName, Object parameter)
			throws ElasticSearchException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String createIndiceMapping(String indexName, String templateName, Object parameter)
			throws ElasticSearchException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String updateIndiceMapping(String action, String templateName, Map parameter) throws ElasticSearchException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String createIndiceMapping(String indexName, String templateName, Map parameter)
			throws ElasticSearchException {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	  * 获取索引定义
	  * @param index
	  * @return
	  * @throws ElasticSearchException
	  */
	 public String getIndice(String index)  throws ElasticSearchException {
		return null;
	 }

}
