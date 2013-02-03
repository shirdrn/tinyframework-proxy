package org.shirdrn.tinyframework.proxy;


/**
 * Any {@link TinyProxyFactory} instance, at least must be able
 * to return a possibly available proxy randomly.
 * 
 * @author Yanjun
 */
public interface TinyProxySeletingService {

	/**
	 * Select a proxy randomly.
	 * @return
	 */
	public abstract TinyProxy select();
}
