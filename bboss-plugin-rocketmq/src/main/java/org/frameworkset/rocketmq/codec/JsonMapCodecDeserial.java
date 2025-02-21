package org.frameworkset.rocketmq.codec;
/**
 * Copyright 2024 bboss
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.frameworkset.util.SimpleStringUtil;
import org.apache.rocketmq.common.message.MessageExt;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * <p>Description: </p>
 * <p></p>
 *
 * @author biaoping.yin
 * @Date 2024/11/20
 */
public class JsonMapCodecDeserial implements CodecDeserial<Map>{

    @SuppressWarnings("unchecked")
    public JsonMapCodecDeserial() {
    }

 

    @Override
    public RocketmqMessage<Map> deserial(MessageExt data) {
        RocketmqMessage<Map> rocketmqMessage = new RocketmqMessage<>();
        rocketmqMessage.setMessageExt(data);
        InputStream inputStream = null;
        try {
            inputStream = new ByteArrayInputStream(data.getBody());
            rocketmqMessage.setData(SimpleStringUtil.json2Object(inputStream,Map.class));
            return rocketmqMessage;
        }
        finally {
            if(inputStream != null){
                try {
                    inputStream.close();
                    inputStream = null;
                } catch (IOException e) {
                }
            }
        }
        
    }
}
