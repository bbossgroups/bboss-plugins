package org.frameworkset.apollo;
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

import org.frameworkset.nacos.PropertiesChangeListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Description: </p>
 * <p></p>
 *
 * @author biaoping.yin
 * @Date 2024/8/28
 */
public class DemoTestListener extends PropertiesChangeListener {
    private Logger logger = LoggerFactory.getLogger(DemoTestListener.class);

    @Override
    public void receiveConfigInfo(String configInfo) {
        logger.info("receiveConfigInfo:{}",configInfo);
    }
}
