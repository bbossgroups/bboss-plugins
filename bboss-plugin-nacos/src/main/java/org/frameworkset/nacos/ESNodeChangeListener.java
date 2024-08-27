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


import org.frameworkset.spi.assemble.AssembleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Properties;
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
	private void poolChange(Properties properties ,String pool){
		boolean isdefault = pool == null || pool.equals("default");
		String hostsKey = null;
		String showDslKey = null;

		if(!isdefault){
			hostsKey = pool+".elasticsearch.rest.hostNames";
			showDslKey = pool + ".elasticsearch.showTemplate";
		}
		else{
			hostsKey = "default.elasticsearch.rest.hostNames";
			showDslKey = "default.elasticsearch.showTemplate";
		}
 

        String _hosts = properties.getProperty(hostsKey);
        if(_hosts == null) {
            _hosts = properties.getProperty(defaultHostsKey);
        }
        //更新hosts
        handleDiscoverHosts(_hosts, pool); 

        String _showDsl = properties.getProperty(showDslKey);
        if(_showDsl == null){
            _showDsl= properties.getProperty(defaultShowDslKey);
        }
        //更新showdsl
        handleShowDsl(_showDsl, pool);

	}
	

    @Override
    public void receiveConfigInfo(String configInfo) {
        if(logger.isInfoEnabled()) {
            logger.info("Changes for namespace {}", this.nacosPropertiesFilePlugin.getNamespace());
        }
        if(elasticsearchPools == null || elasticsearchPools.size() == 0){
            logger.info("Changes for elasticsearch hosts ignored: elasticsearchPools is not setted yet.");
            return;
        }
        try {
            Properties properties = nacosPropertiesFilePlugin.getProperties(configInfo);
            for(String pool:elasticsearchPools) {
                poolChange(properties, pool);
            }
        } catch (IOException e) {
            throw new AssembleException(e);
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
