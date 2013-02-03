package org.shirdrn.tinyframework.proxy.detector;

import org.shirdrn.tinyframework.proxy.TinyProxy;
import org.shirdrn.tinyframework.proxy.TinyProxyDetector;
import org.shirdrn.tinyframework.proxy.TinyProxyFactory;

/**
 * Default proxy checker. 
 * Actually, this {@link TinyProxyDetector} implementation does not check
 * any proxy object at all.
 * 
 * @author Yanjun
 */
public class DefaultProxyDetector extends TinyProxyDetector {

	public DefaultProxyDetector(TinyProxyFactory proxyFactory) {
		super(proxyFactory);
	}

	@Override
	public boolean detect(TinyProxy proxy) {
		return true;
	}

	@Override
	public boolean detect(String host, int port) {
		return true;
	}

}
