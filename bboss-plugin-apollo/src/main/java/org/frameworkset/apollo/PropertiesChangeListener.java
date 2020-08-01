package org.frameworkset.apollo;
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

import com.ctrip.framework.apollo.ConfigChangeListener;
import org.frameworkset.spi.assemble.PropertiesContainer;

/**
 * <p>Description: </p>
 * <p></p>
 * <p>Copyright (c) 2020</p>
 * @Date 2020/8/1 9:48
 * @author biaoping.yin
 * @version 1.0
 */
public abstract class PropertiesChangeListener implements ConfigChangeListener {
	protected PropertiesContainer propertiesContainer;

	public void setPropertiesContainer(PropertiesContainer propertiesContainer) {
		this.propertiesContainer = propertiesContainer;
	}

	public PropertiesContainer getPropertiesContainer() {
		return propertiesContainer;
	}
}
