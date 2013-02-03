package org.shirdrn.tinyframework.proxy.detector;

import org.junit.Before;
import org.junit.Test;
import org.shirdrn.tinyframework.proxy.DefaultProxyFactory;
import org.shirdrn.tinyframework.proxy.TinyProxyDetector;
import org.shirdrn.tinyframework.proxy.TinyProxyFactory;
import org.shirdrn.tinyframework.proxy.conf.Context;
import org.shirdrn.tinyframework.proxy.conf.ReadableContext;

public class TestProxyDetector {

	TinyProxyFactory proxyFactory;
	TinyProxyDetector proxyDetector;
	
	@Before
	public void initialize() {
		ReadableContext readableContext = new Context(false);
		((Context) readableContext).addResource("proxy-default.xml");
		proxyFactory = new DefaultProxyFactory();
		proxyFactory.setReadableContext(readableContext);
	}
	
	@Test
	public void testHttpConversationProxyDetector() {
		proxyDetector = new HttpConversationProxyDetector(proxyFactory);
	}
}
