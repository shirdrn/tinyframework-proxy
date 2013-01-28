package org.shirdrn.tinyframework.commons.proxy;

import org.shirdrn.tinyframework.commons.proxy.factory.ProxyFactory;


/**
 * Check a proxy's availability. 
 * 
 * @author Yanjun
 */
public abstract class ProxyChecker {

	protected ProxyFactory proxyFactory;
	
	public ProxyChecker(ProxyFactory proxyFactory) {
		super();
		this.proxyFactory = proxyFactory;
	}
	
	/**
	 * Check the given {@link Proxy}. If available, then
	 * return a state flag.
	 * @param proxy
	 * @return
	 */
	public abstract boolean check(Proxy proxy);
	
	/**
	 * Check the given proxy. If available, then
	 * return a state flag.
	 * @param proxy
	 * @return
	 */
	public abstract boolean check(String host, int port);
	
}
