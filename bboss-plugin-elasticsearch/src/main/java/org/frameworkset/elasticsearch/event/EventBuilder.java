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

package org.frameworkset.elasticsearch.event;

import java.util.HashMap;
import java.util.Map;

public class EventBuilder  {

  /**
   * Instantiate an Event instance based on the provided body and headers.
   * If <code>headers</code> is <code>null</code>, then it is ignored.
   * @param body
   * @param headers
   * @return
   */
  public static Event withBody(Object body, Map<String, String> headers) {
    Event event = new SimpleEvent(body);

//    if (body == null) {
//      body = null;
//    }
//    event.setBody(body);

    if (headers != null) {
      event.setHeaders(new HashMap<String, String>(headers));
    }

    return event;
  }

  public static Event withBody(Object body) {
    return withBody(body, null);
  }

  

   

}
