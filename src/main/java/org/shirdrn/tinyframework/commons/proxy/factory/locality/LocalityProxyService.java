package org.shirdrn.tinyframework.commons.proxy.factory.locality;

import org.shirdrn.tinyframework.commons.proxy.Proxy;
import org.shirdrn.tinyframework.commons.proxy.ProxyService;

/**
 * Proxy service orienting to who needs locality features, besides
 * implementing a default proxy random selecting policy.
 * 
 * @author Yanjun
 */
public interface LocalityProxyService extends ProxyService {

	/**
	 * Select a proxy based on given country code by using locality features.
	 * @param countryCode
	 * @return
	 */
	public abstract Proxy selectByCountry(String countryCode);
	
	/**
	 * Select a proxy based on given host name by using locality features.
	 * @param host
	 * @return
	 */
	public abstract Proxy selectByHost(String host);
}
