package org.frameworkset.hibernate.serial;

import java.util.ArrayList;
import java.util.List;

import org.frameworkset.soa.PreSerial;
import org.hibernate.Hibernate;

public class PersistentList  implements PreSerial<List> {
	private static final String clazz = "org.hibernate.collection.PersistentList";
	private static final String vclazz = "java.util.ArrayList";
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
	public List prehandle(List object) {
		boolean init = Hibernate.isInitialized(object);  
		  
        if (init) {  
             
            return object;  
         
   
        } else {  
             return new ArrayList();
        }  
	}

	@Override
	public List posthandle(List object) {
		// TODO Auto-generated method stub
		return object;
	}
}
