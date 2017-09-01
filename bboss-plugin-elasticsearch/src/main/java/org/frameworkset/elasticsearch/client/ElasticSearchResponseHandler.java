package org.frameworkset.elasticsearch.client;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.frameworkset.util.SimpleStringUtil;

public class ElasticSearchResponseHandler implements ResponseHandler<SearchResult> {
	private static Logger logger = LoggerFactory.getLogger(ElasticSearchResponseHandler.class);
	
	public ElasticSearchResponseHandler() {
		// TODO Auto-generated constructor stub
	}

	 @Override
     public SearchResult handleResponse(final HttpResponse response)
             throws ClientProtocolException, IOException {
         int status = response.getStatusLine().getStatusCode();

         if (status >= 200 && status < 300) {
             HttpEntity entity = response.getEntity();
             RestResponse searchResponse = null;
             try {

                 searchResponse = entity != null ? SimpleStringUtil.json2Object(entity.getContent(), RestResponse.class) : null;
//                 String content = EntityUtils.toString(entity);
//                 System.out.println(content);
//                 searchResponse = entity != null ? SimpleStringUtil.json2Object(content, RestResponse.class) : null;
             }
             catch (Exception e){
                 logger.error("",e);
             }
//             ClassUtil.ClassInfo classInfo = ClassUtil.getClassInfo(TransportClient.class);
//             NamedWriteableRegistry namedWriteableRegistry = (NamedWriteableRegistry)classInfo.getPropertyValue(clientUtil.getClient(),"namedWriteableRegistry");

             return searchResponse;

         } else {
             HttpEntity entity = response.getEntity();
             if (entity != null ) {
                 ErrorResponse searchResponse = null;
                 try {
                     searchResponse = entity != null ? SimpleStringUtil.json2Object(entity.getContent(), ErrorResponse.class) : null;
                 }
                 catch (Exception e){
                     logger.error("",e);
                 }
                 return searchResponse;
             }
             else
                 throw new ClientProtocolException("Unexpected response status: " + status);
         }
     }

}
