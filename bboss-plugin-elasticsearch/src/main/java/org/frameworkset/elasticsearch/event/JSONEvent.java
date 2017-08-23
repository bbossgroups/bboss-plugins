/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.frameworkset.elasticsearch.event;

import java.util.Map;

/**
 *
 */
public class JSONEvent implements Event<String> {
  private Map<String, String> headers;
  private String body;
  private transient String charset = "UTF-8";
  private long TTL;
  private String indexType;
  private String indexPrefix;

  @Override
  public Map<String, String> getHeaders() {
    return headers;
  }

  @Override
  public void setHeaders(Map<String, String> headers) {
    this.headers = headers;
  }

  @Override
  public String getBody() {
    if (body != null) {
       {
        return body ;
      } 
    } else {
      return "";
    }

  }

  @Override
  public void setBody(String body) {
    if (body != null) {
      this.body =  (body);
    } else {
      this.body = "";
    }
  }

  public void setCharset(String charset) {
    this.charset = charset;
  }

 

	public String getIndexType() {
		return indexType;
	}
	
	public void setIndexType(String indexType) {
		this.indexType = indexType;
	}
	
	public long getTTL() {
		return TTL;
	}
	
	public void setTTL(long tTL) {
		TTL = tTL;
	}
	
	@Override
	public String getIndexPrefix() {
		// TODO Auto-generated method stub
		return indexPrefix;
	}
	
	@Override
	public void setIndexPrefix(String indexPrefix) {
		this.indexPrefix = indexPrefix;
		
	}
 

}
