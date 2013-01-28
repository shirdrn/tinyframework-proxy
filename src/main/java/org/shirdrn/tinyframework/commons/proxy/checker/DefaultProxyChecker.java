package org.shirdrn.tinyframework.commons.proxy.checker;

import org.shirdrn.tinyframework.commons.proxy.Proxy;
import org.shirdrn.tinyframework.commons.proxy.ProxyChecker;
import org.shirdrn.tinyframework.commons.proxy.factory.ProxyFactory;

/**
 * Default proxy checker. 
 * Actually, this {@link ProxyChecker} implementation does not check
 * any proxy object at all.
 * 
 * @author Yanjun
 */
public class DefaultProxyChecker extends ProxyChecker {

	public DefaultProxyChecker(ProxyFactory proxyFactory) {
		super(proxyFactory);
	}

	@Override
	public boolean check(Proxy proxy) {
		return true;
	}

	@Override
	public boolean check(String host, int port) {
		return true;
	}

}
