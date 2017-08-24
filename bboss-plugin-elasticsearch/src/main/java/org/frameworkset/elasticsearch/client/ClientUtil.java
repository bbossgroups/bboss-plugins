package org.frameworkset.elasticsearch.client;

import org.elasticsearch.client.Client;
import org.frameworkset.elasticsearch.ElasticSearchEventSerializer;
import org.frameworkset.elasticsearch.event.Event;

public interface ClientUtil {
	public void  deleteIndex(String indexName,String indexType,String... ids) throws Exception ;
	public  void addEvent(Event event,ElasticSearchEventSerializer  elasticSearchEventSerializer) throws Exception ;
	public void updateIndexs(Event event,ElasticSearchEventSerializer  elasticSearchEventSerializer)throws Exception;
	 public Object execute() throws Exception;

	 public Client getClient();
	 /**
	  * 
	  * @param path
	  * @param string
	  * @return
	  */
	 public Object executeRequest(String path, String entity) throws Exception ;
	 /**
	  * 
	  * @param path
	  * @param string
	  * @return
	  */
	 public Object executeRequest(String path) throws Exception ;
	 
	/**
	  * 
	  * @param path
	  * @param string
	  * @return
	  */
	public String delete(String path, String string);
	
}
