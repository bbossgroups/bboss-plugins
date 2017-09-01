package org.frameworkset.elasticsearch.client;

import java.util.Map;

import org.apache.http.client.ResponseHandler;
import org.elasticsearch.client.Client;
import org.frameworkset.elasticsearch.ElasticSearchEventSerializer;
import org.frameworkset.elasticsearch.ElasticSearchException;
import org.frameworkset.elasticsearch.event.Event;

public interface ClientUtil {
	public final String HTTP_GET = "get";
	public final String HTTP_POST = "post";
	public final String HTTP_DELETE = "delete";
	public final String HTTP_PUT = "put";
	public void  deleteIndex(String indexName,String indexType,String... ids) throws ElasticSearchException ;
	public  void addEvent(Event event,ElasticSearchEventSerializer  elasticSearchEventSerializer) throws ElasticSearchException ;
	public void updateIndexs(Event event,ElasticSearchEventSerializer  elasticSearchEventSerializer)throws ElasticSearchException;
	 public Object execute() throws Exception;

	 public Client getClient();
	 /**
	  * 
	  * @param path
	  * @param entity
	  * @return
	  */
	 public Object executeRequest(String path, String entity) throws ElasticSearchException ;
	 
	 
	 /**
	  * 
	  * @param path
	  * @return
	  */
	 public Object executeRequest(String path) throws ElasticSearchException ;
	 
	 public String executeHttp(String path,String action) throws ElasticSearchException;
		/**
		 * 
		 * @param path
		 * @param entity
		 * @param action get,post,put,delete
		 * @return
		 * @throws ElasticSearchException
		 */
		public String executeHttp(String path, String entity,String action) throws ElasticSearchException ;
	/**
	  * 
	  * @param path
	  * @param string
	  * @return
	  */
	public String delete(String path, String string);
	public String getIndexMapping(String index) throws ElasticSearchException;
	public String executeRequest(String path, String templateName,Map params) throws ElasticSearchException;
	
	 
	public String executeRequest(String path, String templateName,Object params) throws ElasticSearchException;
	
	
	public <T> T executeRequest(String path, String entity,ResponseHandler<T> responseHandler) throws ElasticSearchException;
	public <T> T executeRequest(String path, String templateName,Map params,ResponseHandler<T> responseHandler) throws ElasticSearchException;
	
	 
	public <T> T  executeRequest(String path, String templateName,Object params,ResponseHandler<T> responseHandler) throws ElasticSearchException;
	
	public SearchResult search(String path, String templateName,Map params) throws ElasticSearchException;
	
	 
	public SearchResult search(String path, String templateName,Object params) throws ElasticSearchException;
	/**
	  * 
	  * @param path
	  * @param entity
	  * @return
	  */
	 public SearchResult search(String path, String entity) throws ElasticSearchException ;
	 
	 
}
