package com.frameworkset.common.poolman.hibernate;
import org.frameworkset.spi.DefaultApplicationContext;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Test;

import com.frameworkset.orm.transaction.TransactionManager;

public class TestPlugin {
	@Test
	public void test()
	{
		DefaultApplicationContext context =  DefaultApplicationContext.getApplicationContext("hibernate.cfg.xml"); 
		SessionFactory factory = context.getTBeanObject("sessionFactory", SessionFactory.class);
		TransactionManager tm = new TransactionManager();
		try
		{
			tm.begin();
			//do hibernate operation
			Session  session = factory.openSession();
		
//			Connection con = factory.openSession().connection();
//			con.createStatement();
			tm.commit();
		}
		catch(Exception e)
		{
			
		}
		finally
		{
			tm.release();
		}
	}

}
