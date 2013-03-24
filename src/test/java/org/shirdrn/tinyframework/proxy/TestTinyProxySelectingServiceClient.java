package org.shirdrn.tinyframework.proxy;

import org.junit.Before;
import org.junit.Test;
import org.shirdrn.tinyframework.proxy.conf.Context;
import org.shirdrn.tinyframework.proxy.locality.LocalityFileProxyFactory;

public class TestTinyProxySelectingServiceClient {

	TinyProxySelectingServiceClient client;
	
	@Before
	public void initialize() {
		// create global context instance
		Context readableContext = new Context(true);
		readableContext.addResource("proxy-default.xml");
		readableContext.addResource("proxy-site.xml");
		readableContext.set("tiny.proxy.file.dir", 
				"E:\\Develop\\eclipse-jee-juno-github\\gitworkspace\\tinyframework-proxy\\src\\test\\resources\\proxies");
		readableContext.set("tiny.proxy.detector.class", 
				"org.shirdrn.tinyframework.proxy.detector.DefaultProxyDetector");
		readableContext.set("tiny.proxy.locality.geoip.file", 
				"E:\\Develop\\eclipse-jee-juno-github\\gitworkspace\\tinyframework-proxy\\src\\main\\resources\\geoip\\GeoIP_DATABASE.dat");
		readableContext.set("tiny.proxy.factory.continents.dir", 
				"E:\\Develop\\eclipse-jee-juno-github\\gitworkspace\\tinyframework-proxy\\src\\main\\resources\\continents");
		
		client = TinyProxySelectingServiceClient.getInstance();
	}
	
	@Test
	public void testClient() {
		TinyProxySelectingService proxySelectingService = client.getProxySelectingService(TinyProxySelectingService.class);
		TinyProxy proxy = proxySelectingService.select();
	}
}
