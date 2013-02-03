package org.shirdrn.tinyframework.proxy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.shirdrn.tinyframework.proxy.conf.Context;
import org.shirdrn.tinyframework.proxy.utils.ObjectFactory;

/**
 * A singleton proxy service client.
 * 
 * @author Yanjun
 */
public final class TinyProxySeletingServiceClient {

	private static final Log LOG = LogFactory.getLog(TinyProxySeletingServiceClient.class);
	private final TinyProxyFactory proxyFactory;
	private final static TinyProxySeletingServiceClient INSTANCE;
	static {
		INSTANCE = new TinyProxySeletingServiceClient();
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				if(INSTANCE.proxyFactory!=null) {
					INSTANCE.proxyFactory.close();
				}
			}
		});
	}
	
	private TinyProxySeletingServiceClient() {
		// create global context instance
		Context readableContext = new Context(true);
		readableContext.addResource("proxy-default.xml");
		readableContext.addResource("proxy-site.xml");
		// load class
		String factoryClassName = readableContext.get(
				"tiny.proxy.factory.class", "org.shirdrn.tinyframework.proxy.DefaultProxyFactory");
		proxyFactory = ObjectFactory.getInstance(
				factoryClassName, TinyProxyFactory.class, this.getClass().getClassLoader());
		LOG.info("Proxy factory class;className=" + factoryClassName);
		proxyFactory.setReadableContext(readableContext);
	}
	
	public static TinyProxySeletingServiceClient getInstance() {
		return INSTANCE;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getProxySelectingService(Class<T> proxyServiceClass) {
		LOG.debug("proxyServiceClass=" + proxyServiceClass.getName());
		TinyProxySeletingService factory = INSTANCE.proxyFactory;
		T service = (T) factory;
		return service;
	}
}
