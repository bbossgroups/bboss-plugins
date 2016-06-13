package org.frameworkset.hibernate.serial;

import java.util.Set;
import java.util.TreeSet;

import org.frameworkset.soa.PreSerial;
import org.hibernate.Hibernate;

public class PersistentSortedSet    implements PreSerial<Set> {
	private static final String clazz = "org.hibernate.collection.PersistentSortedSet";
	private static final String vclazz = "java.util.TreeSet";
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
	public Set prehandle(Set object) {
		boolean init = Hibernate.isInitialized(object);  
		  
        if (init) {  
             
            return object;  
         
   
        } else {  
             return new TreeSet();
        }  
	}

	@Override
	public Set posthandle(Set object) {
		// TODO Auto-generated method stub
		return object;
	}

}
