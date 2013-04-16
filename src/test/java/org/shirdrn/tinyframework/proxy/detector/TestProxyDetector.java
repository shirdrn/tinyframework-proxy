package org.shirdrn.tinyframework.proxy.detector;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.shirdrn.tinyframework.proxy.DefaultProxyFactory;
import org.shirdrn.tinyframework.proxy.TinyProxy;
import org.shirdrn.tinyframework.proxy.TinyProxyDetector;
import org.shirdrn.tinyframework.proxy.TinyProxyFactory;
import org.shirdrn.tinyframework.proxy.conf.Context;

public class TestProxyDetector {

	private static final Log LOG = LogFactory.getLog(TestProxyDetector.class);
	TinyProxyFactory factory;
	TinyProxyDetector detector;
	List<TinyProxy> proxies = new ArrayList<TinyProxy>();
	
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
		
		loadProxies();
	}

	private void loadProxies() {
		proxies.add(new TinyProxy("91.213.87.3:3129"));
		proxies.add(new TinyProxy("87.106.246.207:8118"));
		proxies.add(new TinyProxy("69.42.127.69:3080"));
		proxies.add(new TinyProxy("221.6.15.156:82"));
		proxies.add(new TinyProxy("219.83.71.250:8080"));
		proxies.add(new TinyProxy("58.221.129.158:1337"));
		proxies.add(new TinyProxy("58.53.192.218:8123"));
		proxies.add(new TinyProxy("61.145.121.124:89"));
		proxies.add(new TinyProxy("61.182.219.90:8090"));
		proxies.add(new TinyProxy("84.241.58.230:8080"));
	}
	
	@Test
	public void testTelnetProxyDetector() {
		LOG.info("Detect proxy by TelnetProxyDetector:");
		detector = new TelnetProxyDetector(factory);
		for(TinyProxy proxy : proxies) {
			boolean available = detector.detect(proxy);
			LOG.info("Result;available=" + available + "," + proxy);
		}
	}
	
	@Test
	public void testHttpConversationProxyDetector() {
		LOG.info("Detect proxy by HttpConversationProxyDetector:");
		detector = new HttpConversationProxyDetector(factory);
		for(TinyProxy proxy : proxies) {
			boolean available = detector.detect(proxy);
			LOG.info("Result;available=" + available + "," + proxy);
		}
	}
	
	@After
	public void release() {
		factory.close();
	}
	
}
