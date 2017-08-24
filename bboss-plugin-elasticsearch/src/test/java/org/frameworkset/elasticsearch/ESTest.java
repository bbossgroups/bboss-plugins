package org.frameworkset.elasticsearch;

import org.frameworkset.elasticsearch.client.ClientUtil;
import org.frameworkset.spi.DefaultApplicationContext;
import org.junit.Test;

public class ESTest {

	public ESTest() {
		// TODO Auto-generated constructor stub
	}
	@Test
	public void test() throws Exception{
		DefaultApplicationContext context = DefaultApplicationContext.getApplicationContext("conf/elasticsearch.xml");
		ElasticSearch elasticSearchSink = context.getTBeanObject("elasticSearch", ElasticSearch.class);
		ElasticSearch restelasticSearchSink = context.getTBeanObject("restelasticSearch", ElasticSearch.class);
		
		ClientUtil clientUtil = restelasticSearchSink.getClientUtil();
		String entity = "{"+
    "\"aggs\": {"+
    "\"top_tags\": {"+
		    "\"terms\": {"+
		    "\"field\": \"rpc.keyword\","+
		    "\"size\": 30"+
		    "},"+
    "\"aggs\": {"+
		    "\"top_sales_hits\": {"+
		    "\"top_hits\": {"+
		    "\"sort\": ["+
		               "{"+
    "\"collectorAcceptTime\": {"+
		            	    "\"order\": \"desc\""+
		            	        "}"+
    "}"+
    "],"+
    "\"_source\": {"+
		            	    "\"includes\": [ \"collectorAcceptTime\", \"rpc\" ]"+
		            	    	    "},"+
    "\"size\" : 1"+
    "}"+
    "}"+
    "}"+
    "}"+
    "}"+
    "}";
		String response = (String) clientUtil.executeRequest("trace-*/_search?size=0",entity);
		
		System.out.println(response);
		
	}
	@Test
	public void querey() throws Exception
	{
		DefaultApplicationContext context = DefaultApplicationContext.getApplicationContext("conf/elasticsearch.xml");
		ElasticSearch elasticSearchSink = context.getTBeanObject("elasticSearch", ElasticSearch.class);
		ElasticSearch restelasticSearchSink = context.getTBeanObject("restelasticSearch", ElasticSearch.class);
		
		ClientUtil clientUtil = restelasticSearchSink.getClientUtil();
		String entiry = "{\"query\" : {\"term\" : { \"rpc\" : \"content.page\" }}}";
		String response = (String) clientUtil.executeRequest("trace-*/_search",entiry);
		
		System.out.println(response);
	}

}
