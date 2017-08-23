package org.frameworkset.elasticsearch.client;

import org.frameworkset.elasticsearch.ElasticSearchEventSerializer;
import org.frameworkset.elasticsearch.event.Event;

public interface ClientUtil {
	public  void addEvent(Event<Object> event,ElasticSearchEventSerializer  elasticSearchEventSerializer) throws Exception ;
	 public void execute() throws Exception;
}
