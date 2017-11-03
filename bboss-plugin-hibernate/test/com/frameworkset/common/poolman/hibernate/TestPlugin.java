package com.frameworkset.common.poolman.hibernate;

import com.frameworkset.orm.transaction.TransactionManager;
import org.frameworkset.soa.PluginFactory;
import org.frameworkset.spi.DefaultApplicationContext;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.dialect.MySQLDialect;
import org.junit.Test;

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

	@Test
	public void testSerialFactory(){
		PluginFactory hibernatePluginFactory = new org.frameworkset.hibernate.serial.HibernatePluginFactory();
		System.out.println(hibernatePluginFactory.getPlugins());
		String[] plugins = hibernatePluginFactory.getPlugins();
		for(String p:plugins){
			try {
				Class cls  = Class.forName(p);
				System.out.println(cls.newInstance());
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			}

		}
	}

}
