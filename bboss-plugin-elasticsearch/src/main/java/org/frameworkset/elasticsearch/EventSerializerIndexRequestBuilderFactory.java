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

import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.common.io.BytesStream;
import org.frameworkset.elasticsearch.event.Event;
import org.frameworkset.util.FastDateFormat;

/**
 * Default implementation of {@link ElasticSearchIndexRequestBuilderFactory}.
 * It serializes flume events using the
 * {@link ElasticSearchEventSerializer} instance configured on the sink.
 */
public class EventSerializerIndexRequestBuilderFactory
    extends AbstractElasticSearchIndexRequestBuilderFactory {

  protected final ElasticSearchEventSerializer serializer;

  public EventSerializerIndexRequestBuilderFactory(
      ElasticSearchEventSerializer serializer) {
    this(serializer, ElasticSearchIndexRequestBuilderFactory.df);
  }

  protected EventSerializerIndexRequestBuilderFactory(
      ElasticSearchEventSerializer serializer, FastDateFormat fdf) {
    super(fdf);
    this.serializer = serializer;
  }

  @Override
  public void configure(Properties elasticsearchPropes) {
    serializer.configure(elasticsearchPropes);
  }

  

  @Override
  protected void prepareIndexRequest(IndexRequestBuilder indexRequest,
      String indexName, String indexType, Event event) throws IOException {
    BytesStream contentBuilder = serializer.getContentBuilder(event);
    indexRequest.setIndex(indexName)
        .setType(indexType)
        .setSource(contentBuilder.bytes());
  }
}
