package org.shirdrn.tinyframework.proxy.common;

/**
 * Common abstract service for injecting DAO to services
 * which implement this class.
 * 
 * @author Yanjun
 */
public abstract class CommonDatabaseService<T> {
	
	protected T dao;
	
	public CommonDatabaseService() {
		super();
	}
	
	@SuppressWarnings("unchecked")
	protected CommonDatabaseService(Class<T> clazz) {
		super();
		this.dao = (T) DaoConfig.getDaoManager().getDao(clazz);
	}
	
	
}
