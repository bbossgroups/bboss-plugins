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

import java.util.Properties;

import org.frameworkset.elasticsearch.event.Event;

public interface IndexNameBuilder  {
  /**
   * Gets the name of the index to use for an index request
   * @param event
   *          Event which determines index name
   * @return index name of the form 'indexPrefix-indexDynamicName'
   */
  public  String getIndexName(Event event);
  
  /**
   * Gets the prefix of index to use for an index request.
   * @param event
   *          Event which determines index name
   * @return Index prefix name
   */
  public     String getIndexPrefix(Event event);
  public void configure(Properties elasticsearchPropes);
}
