package org.frameworkset.elasticsearch;

import org.frameworkset.spi.DefaultApplicationContext;

public class ElasticSearchHelper {
	private static DefaultApplicationContext context = DefaultApplicationContext.getApplicationContext("conf/elasticsearch.xml");
	public ElasticSearchHelper() {
		// TODO Auto-generated constructor stub
	}
	
	public static ElasticSearch getElasticSearchSink(String name){
		ElasticSearch elasticSearchSink = context.getTBeanObject(name, ElasticSearch.class);
		return elasticSearchSink;
	}
	
	

}
