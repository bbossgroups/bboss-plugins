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

import com.ctrip.framework.apollo.enums.PropertyChangeType;
import com.ctrip.framework.apollo.model.ConfigChange;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.TreeSet;

/**
 * <p>Description:
 * 监听es节点地址清单变化，并将变化更新到客户端es节点地址列表
 * 监听集群路由规则是否变化，并切换到变化后的路由规则组
 * 监听dsl打印控制开关变化，并更新客户端打印dsl控制开关
 * </p>
 * <p></p>
 * <p>Copyright (c) 2020</p>
 * @Date 2020/8/9 23:10
 * @author biaoping.yin
 * @version 1.0
 */
public class ESNodeChangeListener extends PropertiesChangeListener {
	private Set<String> elasticsearchPools;
	private static Logger logger = LoggerFactory.getLogger(ESNodeChangeListener.class);
	private static Method handleDiscoverHosts;
	private static Method handleShowDsl;
	static {
		try {
			Class clazz = Class.forName("org.frameworkset.elasticsearch.client.HostDiscoverUtil");
			handleDiscoverHosts = clazz.getMethod("handleDiscoverHosts", String[].class, String.class);
			handleShowDsl = clazz.getMethod("swithShowdsl", boolean.class, String.class);
		}
		catch (Exception e){

		}
	}
	private String defaultHostsKey = "elasticsearch.rest.hostNames";
	private String defaultShowDslKey = "elasticsearch.showTemplate";
	private void handleDiscoverHosts(String _hosts,String poolName){
		if(_hosts != null && !_hosts.equals("")){
			String[] hosts = _hosts.split(",");

			//将被动获取到的地址清单加入服务地址组poolName中
			if(handleDiscoverHosts != null) {
				try {
					handleDiscoverHosts.invoke(null,hosts,poolName);
				} catch (IllegalAccessException e) {
					logger.error("handleDiscoverHosts failed:hosts["+_hosts+"],pool["+poolName+"]",e);
				} catch (InvocationTargetException e) {
					logger.error("handleDiscoverHosts failed:hosts["+_hosts+"],pool["+poolName+"]",e.getTargetException());
				}
			}
		}
	}

	private void handleShowDsl(String showDsl,String poolName){
		if(showDsl != null && !showDsl.equals("")){

			//将被动获取到的地址清单加入服务地址组poolName中
			if(handleShowDsl != null) {
				try {
					handleShowDsl.invoke(null,showDsl.trim().equals("true"),poolName);
				} catch (IllegalAccessException e) {
					logger.error("handleShowDsl failed:showDsl["+showDsl+"],pool["+poolName+"]",e);
				} catch (InvocationTargetException e) {
					logger.error("handleShowDsl failed:showDsl["+showDsl+"],pool["+poolName+"]",e.getTargetException());
				}
			}
		}
	}
	private void poolChange(ConfigChangeEvent changeEvent ,String pool){
		Set<String> changedKeys = changeEvent.changedKeys();
		ConfigChange hostsChange = null;
		boolean isdefault = pool == null || pool.equals("default");
		String hostsKey = null;
		String showDslKey = null;
		ConfigChange showDslChange = null;

		if(!isdefault){
			hostsKey = pool+".elasticsearch.rest.hostNames";
			showDslKey = pool + ".elasticsearch.showTemplate";
		}
		else{
			hostsKey = "default.elasticsearch.rest.hostNames";
			showDslKey = "default.elasticsearch.showTemplate";
		}

		for (String key : changedKeys) {
			if(key.equals(hostsKey) || (isdefault && key.equals(defaultHostsKey))){//schedule集群
				hostsChange = changeEvent.getChange(key);


			}
			if(key.equals(showDslKey) || (isdefault && key.equals(defaultShowDslKey))){//schedule集群
				showDslChange = changeEvent.getChange(key);
			}





		}
		if(hostsChange != null && hostsChange.getChangeType() == PropertyChangeType.MODIFIED){
			logger.info("Found change - key: {}, oldValue: {}, newValue: {}, changeType: {}",
					hostsChange.getPropertyName(), hostsChange.getOldValue(),
					hostsChange.getNewValue(), hostsChange.getChangeType());

			String _hosts = hostsChange.getNewValue();
			//更新hosts
			handleDiscoverHosts(_hosts, pool);

		}
		if(showDslChange != null && showDslChange.getChangeType() == PropertyChangeType.MODIFIED){
			logger.info("Found change - key: {}, oldValue: {}, newValue: {}, changeType: {}",
					showDslChange.getPropertyName(), showDslChange.getOldValue(),
					showDslChange.getNewValue(), showDslChange.getChangeType());

			String _showDsl = showDslChange.getNewValue();
			//更新showdsl
			handleShowDsl(_showDsl, pool);

		}

	}
	/**
	 * //模拟被动获取监听地址清单
	 * List<HttpHost> hosts = new ArrayList<HttpHost>();
	 * // https服务必须带https://协议头,例如https://192.168.137.1:808
	 * HttpHost host = new HttpHost("192.168.137.1:808");
	 * hosts.add(host);
	 *
	 *    host = new HttpHost("192.168.137.1:809");
	 *    hosts.add(host);
	 *
	 * host = new HttpHost("192.168.137.1:810");
	 * hosts.add(host);
	 * //将被动获取到的地址清单加入服务地址组report中
	 * HttpProxyUtil.handleDiscoverHosts("schedule",hosts);
	 */
	public void onChange(ConfigChangeEvent changeEvent) {
		if(logger.isInfoEnabled()) {
			logger.info("Changes for namespace {}", changeEvent.getNamespace());
		}
		if(elasticsearchPools == null || elasticsearchPools.size() == 0){
			logger.info("Changes for elasticsearch hosts ignored: elasticsearchPools is not setted yet.");
			return;
		}
		for(String pool:elasticsearchPools) {
			poolChange(changeEvent, pool);
		}

	}




	@Override
	public void completeLoaded() {
		elasticsearchPools =  new TreeSet<String>();
		if(this.propertiesContainer != null){
			String poolNames = propertiesContainer.getProperty("elasticsearch.serverNames");
			if(poolNames != null){

				String tmp[] = poolNames.split(",");
				for(int i = 0; i < tmp.length; i ++){
					this.elasticsearchPools.add(tmp[i].trim());
				}
			}
			else{
				this.elasticsearchPools.add("default");
			}
		}
	}
}
