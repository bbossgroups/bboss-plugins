package org.apache.log4j;
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

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

/**
 * <p>Description: 将进程号追加到日志文件名称中，例如：
 *  ecps-crm-calllog.log.15596.20200812204711
 *  其中的15596为进程号，20200812204711为时间戳
 * </p>
 * <p></p>
 * <p>Copyright (c) 2020</p>
 * @Date 2020/8/12 19:16
 * @author biaoping.yin
 * @version 1.0
 */
public class ProcIdRollingFileAppender extends NormalRollingFileAppender{
	/**
	 * 进程号
	 */
	protected String procId;
	public ProcIdRollingFileAppender() {
		procId = this.getProcessID();
	}

	protected String getProcessID(){
		RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
		return runtimeMXBean.getName().split("@")[0];
	}

	public ProcIdRollingFileAppender(Layout layout, String filename, boolean append) throws IOException {
		super(layout, filename, append);
		procId = this.getProcessID();
	}

	public ProcIdRollingFileAppender(Layout layout, String filename) throws IOException {
		super(layout, filename);
		procId = this.getProcessID();
	}
	protected String buildName(String name){
		return name+"."+procId;
	}
}
