package com.frameworkset.common.poolman.hibernate;
import org.frameworkset.spi.DefaultApplicationContext;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.dialect.MySQLDialect;
import org.junit.Test;

import com.frameworkset.orm.transaction.TransactionManager;

public class TestPlugin {
	@Test
	public void test()
	{
		
		TransactionManager tm = new TransactionManager();
		try
		{
			MySQLDialect s;
			DefaultApplicationContext context =  DefaultApplicationContext.getApplicationContext("hibernate.cfg.xml"); 
			SessionFactory factory = context.getTBeanObject("sessionFactory", SessionFactory.class);
			tm.begin();
			//do hibernate operation
			Session  session = factory.openSession();
		
//			Connection con = factory.openSession().connection();
//			con.createStatement();
			tm.commit();
		}
		catch(RuntimeException e)
		{
			e.printStackTrace();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
		finally
		{
			tm.release();
		}
	}

}
