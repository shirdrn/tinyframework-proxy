package org.shirdrn.tinyframework.proxy;



/**
 * Detect a proxy's availability. 
 * 
 * @author Yanjun
 */
public abstract class TinyProxyDetector {

	protected TinyProxyFactory proxyFactory;
	
	public TinyProxyDetector(TinyProxyFactory proxyFactory) {
		super();
		this.proxyFactory = proxyFactory;
	}
	
	/**
	 * Check the given {@link TinyProxy}. If available, then
	 * return a state flag.
	 * @param proxy
	 * @return
	 */
	public abstract boolean detect(TinyProxy proxy);
	
	/**
	 * Check the given proxy. If available, then
	 * return a state flag.
	 * @param proxy
	 * @return
	 */
	public abstract boolean detect(String host, int port);
	
}
