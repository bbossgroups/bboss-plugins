package org.frameworkset.elasticsearch;

import org.frameworkset.spi.DefaultApplicationContext;
import org.junit.Test;

public class ESTest {

	public ESTest() {
		// TODO Auto-generated constructor stub
	}
	@Test
	public void test(){
		DefaultApplicationContext context = DefaultApplicationContext.getApplicationContext("conf/elasticsearch.xml");
		ElasticSearch elasticSearchSink = context.getTBeanObject("elasticSearch", ElasticSearch.class);
	}

}
