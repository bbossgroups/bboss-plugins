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
package org.frameworkset.elasticsearch;

import java.io.IOException;
import java.util.Properties;
import java.util.TimeZone;

import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.client.Client;
import org.frameworkset.elasticsearch.event.Event;
import org.frameworkset.util.FastDateFormat;

/**
 * Interface for creating ElasticSearch {@link IndexRequestBuilder} instances
 * from serialized flume events. This is configurable, so any config params
 * required should be taken through this.
 */
public interface ElasticSearchIndexRequestBuilderFactory {

  static final FastDateFormat df = FastDateFormat.getInstance("yyyy.MM.dd",
      TimeZone.getTimeZone("Asia/Shanghai"));

  /**
   * @param client
   *          ElasticSearch {@link Client} to prepare index from
   * @param indexPrefix
   *          Prefix of index name to use -- as configured on the sink
   * @param indexType
   *          Index type to use -- as configured on the sink
   * @param event
   *          Flume event to serialize and add to index request
   * @return prepared ElasticSearch {@link IndexRequestBuilder} instance
   * @throws IOException
   *           If an error occurs e.g. during serialization
   */
  IndexRequestBuilder createIndexRequest(Client client, String indexPrefix,
      String indexType, Event event) throws IOException;

  public void configure(Properties elasticsearchPropes);

}
