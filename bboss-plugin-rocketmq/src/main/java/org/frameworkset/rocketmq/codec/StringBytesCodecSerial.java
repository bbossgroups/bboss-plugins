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
import org.frameworkset.rocketmq.RockemqException;

import java.io.UnsupportedEncodingException;

/**
 * <p>Description: </p>
 * <p></p>
 *
 * @author biaoping.yin
 * @Date 2024/11/20
 */
public class StringBytesCodecSerial implements CodecSerial<byte[]>{

    @Override
    public byte[] serial(Object data) {
        if(data == null){
            return null;
        }
        String str = null;
        if(data instanceof String){
            str = ((String)data);
        }
        else{
            str = SimpleStringUtil.object2json(data);
            
        }
        try {
            return str.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RockemqException(e);
        }
    }
}