package org.frameworkset.elasticsearch.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.http.client.ResponseHandler;
import org.elasticsearch.client.Client;
import org.frameworkset.elasticsearch.ElasticSearchEventSerializer;
import org.frameworkset.elasticsearch.ElasticSearchException;
import org.frameworkset.elasticsearch.IndexNameBuilder;
import org.frameworkset.elasticsearch.event.Event;
import org.frameworkset.spi.remote.http.MapResponseHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.frameworkset.util.SimpleStringUtil;

public class RestClientUtil implements ClientUtil{
	private static Logger logger = LoggerFactory.getLogger(RestClientUtil.class);
	protected ElasticSearchRestClient client;
	protected StringBuilder bulkBuilder;
	protected IndexNameBuilder indexNameBuilder;
	public RestClientUtil(ElasticSearchClient client,IndexNameBuilder indexNameBuilder) {
		this.client = (ElasticSearchRestClient)client;
		this.indexNameBuilder = indexNameBuilder;
	}
	public   void addEvent(Event event,ElasticSearchEventSerializer elasticSearchEventSerializer) throws ElasticSearchException {
	    if (bulkBuilder == null) {
	    	 bulkBuilder = new StringBuilder();
	    }
	    client.createIndexRequest(bulkBuilder, indexNameBuilder, event,  elasticSearchEventSerializer);
	     
	  }

	@Override
	public void updateIndexs(Event event, ElasticSearchEventSerializer elasticSearchEventSerializer) throws ElasticSearchException {

	}
	@Override
	public String executeRequest(String path, String templateName,Map params) throws ElasticSearchException {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override 
	public String executeRequest(String path, String templateName,Object params) throws ElasticSearchException {
		// TODO Auto-generated method stub
				return null;
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
	public Object execute() throws ElasticSearchException {
		  return client.execute(this.bulkBuilder.toString());
	  }

	@Override
	public Client getClient() {
		return null;
	}

	@Override
	public void deleteIndex(String indexName, String indexType, String... ids) throws ElasticSearchException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public String executeRequest(String path, String entity) throws ElasticSearchException {
		// TODO Auto-generated method stub
		return this.client.executeRequest(path,entity);
	}
	
	@Override
	public <T> T executeRequest(String path, String entity,ResponseHandler<T> responseHandler) throws ElasticSearchException {
		// TODO Auto-generated method stub
		return this.client.executeRequest(path,entity,  responseHandler);
	}
	
	
	@Override
	public String executeHttp(String path, String action) throws ElasticSearchException {
		// TODO Auto-generated method stub
		return this.client.executeHttp(path,action);
	}
	@Override
	public String executeHttp(String path, String entity,String action) throws ElasticSearchException {
		// TODO Auto-generated method stub
		return this.client.executeHttp(path,entity);
	}
	
	public String getIndexMapping(String index) throws ElasticSearchException{
		return this.client.executeHttp(index+"/_mapping?pretty",ClientUtil.HTTP_GET);
	}
	@Override
	public String delete(String path, String string) {
		// TODO Auto-generated method stub
		return null;
	}

	public <T> T executeRequest(String path, String templateName,Map params,ResponseHandler<T> responseHandler) throws ElasticSearchException{
		return null;
	}
	
	 
	public <T> T  executeRequest(String path, String templateName,Object params,ResponseHandler<T> responseHandler) throws ElasticSearchException{
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
		 
		return executeRequest(  path,   entity,new ElasticSearchResponseHandler());
	}
	
	
	 public Map<String,Object>  searchMap(String path, String templateName,Map params) throws ElasticSearchException{
		 return null;
	 }
		
	 
		public Map<String,Object> searchMap(String path, String templateName,Object params) throws ElasticSearchException{
			return null;
		}
		/**
		  * 
		  * @param path
		  * @param entity
		  * @return
		  */
		 @SuppressWarnings("unchecked")
		public Map<String,Object> searchMap(String path, String entity) throws ElasticSearchException {
			 return executeRequest(  path,   entity,new MapResponseHandler());
		 }
	
	 public String dropIndice(String index)  throws ElasticSearchException {
		 return this.client.executeHttp(index+"?pretty",ClientUtil.HTTP_DELETE);
		 
	 }
	 
	 
	 /**
	  * 更新索引定义
	  * @param indexMapping
	  * @return
	  * @throws ElasticSearchException
	  */
	 public String updateIndiceMapping(String action,String indexMapping)  throws ElasticSearchException {
		 return this.client.executeHttp(action,indexMapping,ClientUtil.HTTP_POST);
	 }
	 
	 /**
	  * 创建索引定义
	  * curl -XPUT 'localhost:9200/test?pretty' -H 'Content-Type: application/json' -d'
		{
		    "settings" : {
		        "number_of_shards" : 1
		    },
		    "mappings" : {
		        "type1" : {
		            "properties" : {
		                "field1" : { "type" : "text" }
		            }
		        }
		    }
		}
	  * @param indexMapping
	  * @return
	  * @throws ElasticSearchException
	  */
	 public String createIndiceMapping(String indexName,String indexMapping)  throws ElasticSearchException {
		 return this.client.executeHttp(indexName+"?pretty",indexMapping,ClientUtil.HTTP_POST);
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
		 String response = (String)client.executeHttp(index+"/_mapping?pretty",ClientUtil.HTTP_GET);
		 return response;
	 }
	 public List<ESIndice> getIndexes() throws ElasticSearchException{
		 String data = this.client.executeHttp("_cat/indices?v",HTTP_GET);
         logger.debug(data);

         if(SimpleStringUtil.isNotEmpty(data)){
			try {
				List<ESIndice> indices = extractIndice(data);
				 return indices;
			} catch (IOException e) {
				throw new ElasticSearchException(e);
			}
        	
         }
         return null;
	 }
	 
	 public List<ESIndice> extractIndice(String data) throws IOException {
	        Reader reader = null;
	        BufferedReader br = null;
	        try{
	        	reader = new StringReader(data);
	            br = new BufferedReader(reader);
		        List<ESIndice> indices = new ArrayList<ESIndice>();
		        int i = 0;
		        SimpleDateFormat format = new SimpleDateFormat(client.getDateFormat());
		        while(true){
		            String line = br.readLine();
		            if(line == null)
		                break;
		            if(i == 0){
		                i ++;
		                continue;
		            }
		            
		            ESIndice esIndice = buildESIndice(  line,  format);
		            //如果索引已经过时，则清除过时索引数据
//		            if(esIndice.getGenDate() != null && esIndice.getGenDate().before(deadLine)){
		                indices.add(esIndice);
//		            }
		
		        }
		        return indices;
	        }
	        finally
	        {
	        	 if(reader != null)
					try {
						reader.close();
					} catch (IOException e) {
						 
					}
	        	 if(br != null)
	        		 try {
	 					br.close();
	 				} catch (IOException e) {
	 					 
	 				}
	        }


	    }
	    
	    private ESIndice buildESIndice(String line,SimpleDateFormat format)
	    {
	    	StringBuilder token = new StringBuilder();
	        ESIndice esIndice = new ESIndice();

	        int k = 0;
	        for(int j = 0; j < line.length(); j ++){
	            char c = line.charAt(j);
	            if(c != ' '){
	                token.append(c);
	            }
	            else {
	                if(token.length() == 0)
	                    continue;
	                switch (k ){
	                    case 0:
	                        esIndice.setHealth(token.toString());
	                        token.setLength(0);
	                        k ++;
	                        break;
	                    case 1:
	                        esIndice.setStatus(token.toString());
	                        token.setLength(0);
	                        k ++;
	                        break;
	                    case 2:
	                        esIndice.setIndex(token.toString());
	                        putGendate(  esIndice,  format);
	                        token.setLength(0);
	                        k ++;
	                        break;
	                    case 3:
	                        esIndice.setUuid(token.toString());
	                        token.setLength(0);
	                        k ++;
	                        break;
	                    case 4:
	                        esIndice.setPri(Integer.parseInt(token.toString()));
	                        token.setLength(0);
	                        k ++;
	                        break;
	                    case 5:
	                        esIndice.setRep(Integer.parseInt(token.toString()));
	                        token.setLength(0);
	                        k ++;
	                        break;
	                    case 6:
	                        esIndice.setDocsCcount(Long.parseLong(token.toString()));
	                        token.setLength(0);
	                        k ++;
	                        break;
	                    case 7:
	                        esIndice.setDocsDeleted(Long.parseLong(token.toString()));
	                        token.setLength(0);
	                        k ++;
	                        break;
	                    case 8:
	                        esIndice.setStoreSize(token.toString());
	                        token.setLength(0);
	                        k ++;
	                        break;
	                    case 9:
	                        esIndice.setPriStoreSize(token.toString());
	                        token.setLength(0);
	                        k ++;
	                        break;
	                    default:
	                        break;

	                }
	            }
	        }
	        esIndice.setPriStoreSize(token.toString());
	        return esIndice;
	    }
	    private void putGendate(ESIndice esIndice,SimpleDateFormat format){
	    	int dsplit = esIndice.getIndex().lastIndexOf('-');

	        try {
	        	if(dsplit > 0){
		            String date = esIndice.getIndex().substring(dsplit+1);
		            esIndice.setGenDate((Date)format.parseObject(date));
	        	}

	        } catch (Exception e) {

	        }
	    }
	    public String refreshIndexInterval(String indexName,int interval) throws ElasticSearchException{
	    	return this.client.executeHttp(new StringBuilder().append(indexName).append("/_settings").toString(), new StringBuilder().append("{  \"index\" : {  \"refresh_interval\" : \"").append(interval).append("s\"   } }").toString(), HTTP_PUT);
	    }
	    public String refreshIndexInterval(String indexName,String indexType,int interval) throws ElasticSearchException{
	    	return this.client.executeHttp(new StringBuilder().append(indexName).append("/").append(indexType).append("/_settings").toString(), new StringBuilder().append("{  \"index\" : {  \"refresh_interval\" : \"").append(interval).append("s\"    } }").toString(), HTTP_PUT);
	    }
	    
	    public String refreshIndexInterval(int interval,boolean preserveExisting) throws ElasticSearchException{
	    	if(preserveExisting)
	    		return this.client.executeHttp("_all/_settings?preserve_existing=true", new StringBuilder().append("{  \"index\" : {  \"refresh_interval\" : \"").append(interval).append("s\"    } }").toString(), HTTP_PUT);
	    	else
	    		return this.client.executeHttp("_all/_settings?preserve_existing=false", new StringBuilder().append("{  \"index\" : {  \"refresh_interval\" : \"").append(interval).append("s\"    } }").toString(), HTTP_PUT);
	    }
	    
	    public String refreshIndexInterval(int interval) throws ElasticSearchException{
	    	return refreshIndexInterval(interval,false);
	    }
		 
 
}
