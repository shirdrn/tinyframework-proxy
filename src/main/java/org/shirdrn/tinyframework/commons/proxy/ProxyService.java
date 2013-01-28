package org.shirdrn.tinyframework.commons.proxy;

import org.shirdrn.tinyframework.commons.proxy.factory.ProxyFactory;

/**
 * Any {@link ProxyFactory} instance, at least must be able
 * to return a possibly available proxy randomly.
 * 
 * @author Yanjun
 */
public interface ProxyService {

	/**
	 * Select a proxy randomly.
	 * @return
	 */
	public abstract Proxy select();
}
