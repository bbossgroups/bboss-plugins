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

import org.apache.rocketmq.common.message.MessageExt;

/**
 * <p>Description: </p>
 * <p></p>
 *
 * @author biaoping.yin
 * @Date 2024/11/20
 */
public class BytesCodecDeserial implements CodecDeserial<byte[]>{

    @Override
    public RocketmqMessage<byte[]> deserial(MessageExt data) {
        RocketmqMessage<byte[]> rocketmqMessage = new RocketmqMessage<>(data.getBody(),data);
        
        return rocketmqMessage;
    }
}
