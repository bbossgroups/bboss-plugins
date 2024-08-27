package org.frameworkset.nacos;
/**
 * Copyright 2020 bboss
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

import com.alibaba.nacos.api.config.listener.Listener;
import org.frameworkset.config.BasePropertiesChangeListener;

import java.util.concurrent.Executor;

/**
 * <p>Description: </p>
 * <p></p>
 * <p>Copyright (c) 2020</p>
 * @date 2020/8/1 9:48
 * @author biaoping.yin
 * @version 1.0
 */
public abstract class PropertiesChangeListener extends BasePropertiesChangeListener implements Listener {
    protected NacosPropertiesFilePlugin nacosPropertiesFilePlugin;
    /**
     * Get executor for execute this receive.
     *
     * @return Executor
     */
    public Executor getExecutor(){
        return null;
    }

    public NacosPropertiesFilePlugin getNacosPropertiesFilePlugin() {
        return nacosPropertiesFilePlugin;
    }

    public void setNacosPropertiesFilePlugin(NacosPropertiesFilePlugin nacosPropertiesFilePlugin) {
        this.nacosPropertiesFilePlugin = nacosPropertiesFilePlugin;
    }
}
