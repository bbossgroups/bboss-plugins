package org.frameworkset.hibernate.serial;

import org.frameworkset.soa.PreSerial;
import org.hibernate.Hibernate;

import java.util.HashMap;
import java.util.Map;

public class PersistentMap  implements PreSerial<Map> {
	private static final String clazz = "org.hibernate.collection.internal.PersistentMap";
	private static final String vclazz = "java.util.HashMap";
	@Override
	public String getClazz() {
		// TODO Auto-generated method stub
		return clazz;
	}
	
	public String getVClazz() {
		// TODO Auto-generated method stub
		return vclazz;
	}

	@Override
	public Map prehandle(Map object) {
		boolean init = Hibernate.isInitialized(object);  
		  
        if (init) {  
             
            return object;  
         
   
        } else {  
             return new HashMap();
        }  
	}

	@Override
	public Map posthandle(Map object) {
		// TODO Auto-generated method stub
		return object;
	}
}
