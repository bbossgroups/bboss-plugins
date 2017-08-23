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
import com.fasterxml.jackson.core.JsonParseException;
import org.elasticsearch.common.xcontent.*;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Utility methods for using ElasticSearch {@link XContentBuilder}
 */
public class ContentBuilderUtil {

  public static final Charset charset = Charset.forName("UTF-8");

  protected ContentBuilderUtil() {
  }

  public static void appendField(XContentBuilder builder, String field,
      byte[] data) throws IOException {
    XContentType contentType = XContentFactory.xContentType(data);
    if (contentType == null) {
      addSimpleField(builder, field, data);
    } else {
      addComplexField(builder, field, contentType, data);
    }
    //addSimpleField(builder, field, data);
  }



  public static void addSimpleField(XContentBuilder builder, String fieldName,
      byte[] data) throws IOException {
    builder.field(fieldName, new String(data, charset));
  }

  public static void addComplexField(XContentBuilder builder, String fieldName,
      XContentType contentType, byte[] data) throws IOException {
    XContentParser parser = null;
    try {
      // Elasticsearch will accept JSON directly but we need to validate that
      // the incoming event is JSON first. Sadly, the elasticsearch JSON parser
      // is a stream parser so we need to instantiate it, parse the event to
      // validate it, then instantiate it again to provide the JSON to
      // elasticsearch.
      // If validation fails then the incoming event is submitted to
      // elasticsearch as plain text.
      parser = XContentFactory.xContent(contentType).createParser(NamedXContentRegistry.EMPTY,data);
      while (parser.nextToken() != null) {};

      // If the JSON is valid then include it
      try {
        parser = XContentFactory.xContent(contentType).createParser(NamedXContentRegistry.EMPTY, data);
        // Add the field name, but not the value.
        builder.field(fieldName);
        // This will add the whole parsed content as the value of the field.
        builder.copyCurrentStructure(parser);
      }
      finally {
        if(parser != null) {
          parser.close();
        }
      }

    } catch (JsonParseException ex) {
      // If we get an exception here the most likely cause is nested JSON that
      // can't be figured out in the body. At this point just push it through
      // as is
      addSimpleField(builder, fieldName, data);
    } finally {
      if (parser != null) {
        parser.close();
      }
    }
  }
}
