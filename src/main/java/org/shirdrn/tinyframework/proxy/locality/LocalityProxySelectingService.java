package org.shirdrn.tinyframework.proxy.locality;

import org.shirdrn.tinyframework.proxy.TinyProxy;
import org.shirdrn.tinyframework.proxy.TinyProxySelectingService;

/**
 * Proxy service orienting to who needs locality features, besides
 * implementing a default proxy random selecting policy.
 * 
 * @author Yanjun
 */
public interface LocalityProxySelectingService extends TinyProxySelectingService {

	/**
	 * Select a proxy based on given country code by using locality features.
	 * @param countryCode
	 * @return
	 */
	public abstract TinyProxy selectByCountry(String countryCode);
	
	/**
	 * Select a proxy based on given host name by using locality features.
	 * @param host
	 * @return
	 */
	public abstract TinyProxy selectByHost(String host);
}
