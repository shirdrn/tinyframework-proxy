package org.shirdrn.tinyframework.proxy.database.service;

import static org.junit.Assert.*;

import org.junit.Test;
import org.shirdrn.tinyframework.proxy.database.model.ProxyDatumExample;
import org.shirdrn.tinyframework.proxy.database.service.impl.ProxyDatumServiceImpl;

public class TestProxyDatumService {

	@Test
	public void selectByExample() {
		ProxyDatumService proxyDatumService = new ProxyDatumServiceImpl();
		assertEquals(true, 
				proxyDatumService.selectByExample(new ProxyDatumExample()).size() == 0);
	}
}
