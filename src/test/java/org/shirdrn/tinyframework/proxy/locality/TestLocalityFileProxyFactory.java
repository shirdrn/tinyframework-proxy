package org.shirdrn.tinyframework.proxy.locality;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.shirdrn.tinyframework.proxy.TinyProxy;
import org.shirdrn.tinyframework.proxy.conf.Context;


public class TestLocalityFileProxyFactory {


	private static final Log LOG = LogFactory.getLog(TestLocalityFileProxyFactory.class);
	LocalityProxyFactory factory;
	
	@Before
	public void initialize() {
		factory = new LocalityFileProxyFactory();
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
	
	@Test
	public void selectByCountry() {
		String[] countryCodes = new String[] {
			"US", "CN", "UK", "RU", "FR",
			"KR", "AU", "TK", "NL", "DE"
		};
		for(int i=0; i<countryCodes.length; i++) {
			TinyProxy proxy = factory.selectByCountry(countryCodes[i]);
			LOG.info("select(" + countryCodes[i] + ")=[" + proxy + "]");
		}
	}
	
	@Test
	public void selectByHost() {
		String[] hosts = new String[] {
			"baidu.com", "ebay.com", 
			"0000000.ru", "amazon.com",
			"baidu.jp", "google.hk",
			"usbank.com", "office.uk"
			};
		for(int i=0; i<hosts.length; i++) {
			TinyProxy proxy = factory.selectByHost(hosts[i]);
			LOG.info("select(" + hosts[i] + ")=[" + proxy + "]");
		}
	}
	
	@After
	public void release() {
		factory.close();
	}
	

}
