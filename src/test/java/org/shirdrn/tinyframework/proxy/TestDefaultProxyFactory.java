package org.shirdrn.tinyframework.proxy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.shirdrn.tinyframework.proxy.conf.Context;

public class TestDefaultProxyFactory {

	private static final Log LOG = LogFactory.getLog(TestDefaultProxyFactory.class);
	private TinyProxyFactory factory;
	
	@Before
	public void initialize() {
		factory = new DefaultProxyFactory();
		// create global context instance
		Context readableContext = new Context(true);
		readableContext.addResource("proxy-default.xml");
		readableContext.addResource("proxy-site.xml");
		readableContext.set("tiny.proxy.file.dir", 
				"E:\\Develop\\eclipse-jee-juno-github\\gitworkspace\\tinyframework-proxy\\src\\test\\resources\\proxies");
		readableContext.set("tiny.proxy.detector.class", 
				"org.shirdrn.tinyframework.proxy.detector.DefaultProxyDetector");
		factory.setReadableContext(readableContext);
		LOG.info("factory=" + factory);
	}
	
	@Test
	public void select() {
		for(int i=1; i<=10; i++) {
			TinyProxy proxy = factory.select();
			LOG.info("select(" + i + ")=[" + proxy + "]");
		}
	}
	
	@After
	public void release() {
		factory.close();
	}
	
	
}
