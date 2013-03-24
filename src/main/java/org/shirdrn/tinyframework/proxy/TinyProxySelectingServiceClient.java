package org.shirdrn.tinyframework.proxy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.shirdrn.tinyframework.proxy.conf.Context;
import org.shirdrn.tinyframework.proxy.conf.ReadableContext;
import org.shirdrn.tinyframework.proxy.utils.ObjectFactory;

/**
 * A singleton proxy service client.
 * 
 * @author Yanjun
 */
public final class TinyProxySelectingServiceClient {

	private static final Log LOG = LogFactory.getLog(TinyProxySelectingServiceClient.class);
	private final TinyProxyFactory factory;
	private final static TinyProxySelectingServiceClient INSTANCE;
	static {
		INSTANCE = new TinyProxySelectingServiceClient();
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				if(INSTANCE.factory!=null) {
					INSTANCE.factory.close();
				}
			}
		});
	}
	
	private Context readableContext;
	
	public ReadableContext getReadableContext() {
		return readableContext;
	}
	
	private TinyProxySelectingServiceClient() {
		// create global context instance
		readableContext = new Context(true);
		readableContext.addResource("proxy-default.xml");
		readableContext.addResource("proxy-site.xml");
		// load class
		String factoryClassName = readableContext.get(
				"tiny.proxy.factory.class", "org.shirdrn.tinyframework.proxy.DefaultProxyFactory");
		factory = ObjectFactory.getInstance(
				factoryClassName, TinyProxyFactory.class, this.getClass().getClassLoader());
		LOG.info("Proxy factory class;className=" + factoryClassName);
		factory.setReadableContext(readableContext);
	}
	
	public static TinyProxySelectingServiceClient getInstance() {
		return INSTANCE;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getProxySelectingService(Class<T> proxyServiceClass) {
		LOG.debug("proxyServiceClass=" + proxyServiceClass.getName());
		TinyProxySelectingService factory = INSTANCE.factory;
		T service = (T) factory;
		return service;
	}
}
