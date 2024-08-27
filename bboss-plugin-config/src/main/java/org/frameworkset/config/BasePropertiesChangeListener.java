package org.frameworkset.config;
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

import org.frameworkset.spi.BaseApplicationContext;
import org.frameworkset.spi.assemble.GetProperties;
import org.frameworkset.spi.assemble.PropertiesContainer;

/**
 * <p>Description: </p>
 * <p></p>
 * <p>Copyright (c) 2020</p>
 * @date 2020/8/1 9:48
 * @author biaoping.yin
 * @version 1.0
 */
public abstract class BasePropertiesChangeListener  {
	protected PropertiesContainer propertiesContainer;

    protected BaseApplicationContext applicationContext;
    public BaseApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public void setApplicationContext(BaseApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }


	public GetProperties getGetProperties() {
		return getProperties;
	}

	public void setGetProperties(GetProperties getProperties) {
		this.getProperties = getProperties;
	}

	protected GetProperties getProperties;
	public void setPropertiesContainer(PropertiesContainer propertiesContainer) {
		this.propertiesContainer = propertiesContainer;
	}

	public PropertiesContainer getPropertiesContainer() {
		return propertiesContainer;
	}
	public void completeLoaded(){

	}
}
