package org.shirdrn.tinyframework.commons.proxy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.shirdrn.tinyframework.commons.core.conf.Context;
import org.shirdrn.tinyframework.commons.core.utils.ObjectFactory;
import org.shirdrn.tinyframework.commons.proxy.factory.ProxyFactory;

/**
 * A singleton proxy service client.
 * 
 * @author Yanjun
 */
public class ProxyServiceClient {

	private static final Log LOG = LogFactory.getLog(ProxyServiceClient.class);
	protected final ProxyFactory proxyFactory;
	private final static ProxyServiceClient INSTANCE;
	static {
		INSTANCE = new ProxyServiceClient();
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				if(INSTANCE.proxyFactory!=null) {
					INSTANCE.proxyFactory.close();
				}
			}
		});
	}
	
	private ProxyServiceClient() {
		// create global context instance
		Context readableContext = new Context(true);
		readableContext.addResource("proxy-default.xml");
		readableContext.addResource("proxy-commons.xml");
		// load class
		String factoryClassName = readableContext.get(
				"commons.proxy.factory.class", "com.comodo.wm.commons.proxy.DefaultProxyFactory");
		proxyFactory = ObjectFactory.getInstance(
				factoryClassName, ProxyFactory.class, this.getClass().getClassLoader());
		LOG.info("Proxy factory class;className=" + factoryClassName);
		proxyFactory.setReadableContext(readableContext);
	}
	
	public static ProxyServiceClient getInstance() {
		return INSTANCE;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getProxyService(Class<T> proxyServiceClass) {
		LOG.debug("proxyServiceClass=" + proxyServiceClass.getName());
		ProxyService factory = INSTANCE.proxyFactory;
		T service = (T) factory;
		return service;
	}
}
