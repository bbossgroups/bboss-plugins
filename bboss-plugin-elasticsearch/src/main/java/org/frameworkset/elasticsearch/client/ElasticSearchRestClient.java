/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.frameworkset.elasticsearch.client;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.elasticsearch.common.bytes.BytesReference;
import org.frameworkset.elasticsearch.ElasticSearchEventSerializer;
import org.frameworkset.elasticsearch.IndexNameBuilder;
import org.frameworkset.elasticsearch.event.Event;
import org.frameworkset.spi.remote.http.HttpRequestUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.VisibleForTesting;
import com.google.gson.Gson;

/**
 * Rest ElasticSearch client which is responsible for sending bulks of events to
 * ElasticSearch using ElasticSearch HTTP API. This is configurable, so any
 * config params required should be taken through this.
 */
public class  ElasticSearchRestClient implements ElasticSearchClient {

  public static final String INDEX_OPERATION_NAME = "index";
  public static final String INDEX_PARAM = "_index";
  public static final String TYPE_PARAM = "_type";
  public static final String TTL_PARAM = "_ttl";
  public static final String BULK_ENDPOINT = "_bulk";
  private Properties extendElasticsearchPropes;
  private static final Logger logger = LoggerFactory.getLogger(ElasticSearchRestClient.class);

  private final ElasticSearchEventSerializer serializer;
  private final RoundRobinList<String> serversList;
  private String elasticUser;
  private String elasticPassword;
  private HttpClient httpClient;
  private Map<String,String> headers = new HashMap<>();
  public void init(){
    //Authorization
    if(elasticUser != null && !elasticUser.equals(""))
      headers.put("Authorization",getHeader(  elasticUser,  elasticPassword) );


  }
  private String getHeader(String user,String password) {
    String auth = user+":"+password;
    byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("US-ASCII")));
    return "Basic " + new String(encodedAuth);
  }
  public ElasticSearchRestClient(String[] hostNames,String elasticUser,String elasticPassword,
      ElasticSearchEventSerializer serializer,Properties extendElasticsearchPropes) {
	this.extendElasticsearchPropes = extendElasticsearchPropes;
    for (int i = 0; i < hostNames.length; ++i) {
      if (!hostNames[i].contains("http://") && !hostNames[i].contains("https://")) {
        hostNames[i] = "http://" + hostNames[i];
      }
    }
    this.serializer = serializer;

    serversList = new RoundRobinList<String>(Arrays.asList(hostNames));
    httpClient = new DefaultHttpClient();
    this.elasticUser = elasticUser;
    this.elasticPassword = elasticPassword;
    this.init();
  }

  @VisibleForTesting
  public ElasticSearchRestClient(String[] hostNames,String elasticUser,String elasticPassword,
          ElasticSearchEventSerializer serializer, HttpClient client,Properties extendElasticsearchPropes) {
    this(hostNames,  elasticUser,  elasticPassword, serializer,  extendElasticsearchPropes);
    httpClient = client;
  }

  @Override
  public void configure(Properties elasticsearchPropes) {
  }

  @Override
  public void close() {
  }

  public  void createIndexRequest(StringBuilder bulkBuilder,IndexNameBuilder indexNameBuilder, Event event,ElasticSearchEventSerializer elasticSearchEventSerializer) throws IOException {

	  BytesReference content = elasticSearchEventSerializer == null?serializer.getContentBuilder(event).bytes():
		  elasticSearchEventSerializer.getContentBuilder(event).bytes()
		  ;
	    Map<String, Map<String, String>> parameters = new HashMap<String, Map<String, String>>();
	    Map<String, String> indexParameters = new HashMap<String, String>();
	    indexParameters.put(ElasticSearchRestClient.INDEX_PARAM, indexNameBuilder.getIndexName(event));
	    indexParameters.put(ElasticSearchRestClient.TYPE_PARAM, event.getIndexType());
	    if (event.getTTL() > 0) {
	      indexParameters.put(ElasticSearchRestClient.TTL_PARAM, Long.toString(event.getTTL())+"ms");
	    }
	    parameters.put(ElasticSearchRestClient.INDEX_OPERATION_NAME, indexParameters);

	    Gson gson = new Gson();
	    
	      bulkBuilder.append(gson.toJson(parameters));
	      bulkBuilder.append("\n");
//	      bulkBuilder.append(content.toBytesArray().toUtf8());
	      bulkBuilder.append(content.utf8ToString());
	      bulkBuilder.append("\n");

	}
  

//  private void initHttpRequest(HttpPost httpRequest){
//    if (headers != null && headers.size() > 0) {
//      Iterator<Map.Entry<String, String>> entries = headers.entrySet().iterator();
//      while (entries.hasNext()) {
//        Map.Entry<String, String> entry = entries.next();
//        httpRequest.addHeader(entry.getKey(), entry.getValue());
//      }
//    }
//  }
  
  public String execute(String entity) throws Exception {
    int  triesCount = 0;
    String response = null;
    Exception e = null;
    while (true) {
      
      String host = serversList.get();
      String url = host + "/" + BULK_ENDPOINT;
      try{
    	  response = HttpRequestUtil.sendJsonBody(entity, url,this.headers);
    	  if (response != null) {
    		  
	        logger.info("Status message from elasticsearch: " + response);
	       
	      }
    	  break;
      }
      catch(Exception ex){
    	  if(triesCount < serversList.size()){//失败尝试下一个地址
    		  triesCount++;
    		  continue;
    	  }
    	  else
    	  {
    		  e = ex;
    		  break;
    	  }
      } 
     
      
     
    }
    if(e != null)
  	  throw e;
    return response;
    

//    if (statusCode != HttpStatus.SC_OK) {
//      if (response.getEntity() != null) {
//        throw new EventDeliveryException(EntityUtils.toString(response.getEntity(), "UTF-8"));
//      } else {
//        throw new EventDeliveryException("Elasticsearch status code was: " + statusCode);
//      }
//    }
  }
@Override
public ClientUtil getClientUtil(IndexNameBuilder indexNameBuilder) {
	// TODO Auto-generated method stub
	return new RestClientUtil(this, indexNameBuilder);
}
public String executeRequest(String path, String entity) throws Exception {
	 int  triesCount = 0;
	    String response = null;
	    Exception e = null;
	    while (true) {
	      
	      String host = serversList.get();
	      String url = new StringBuilder().append(host ).append( "/" ).append( path).toString();
	      try{
	    	  if(entity == null)
	    		  response = HttpRequestUtil.httpPostforString(url, null,this.headers);
	    	  else
	    		  response = HttpRequestUtil.sendJsonBody(entity, url,this.headers);
	    	  if (response != null) {
	    		  
		        logger.info("Status message from elasticsearch: " + response);
		       
		      }
	    	  break;
	      }
	      catch(Exception ex){
	    	  if(triesCount < serversList.size()){//失败尝试下一个地址
	    		  triesCount++;
	    		  continue;
	    	  }
	    	  else
	    	  {
	    		  e = ex;
	    		  break;
	    	  }
	      } 
	     
	      
	     
	    }
	    if(e != null)
	  	  throw e;
	    return response;
}
}
