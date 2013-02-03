package org.shirdrn.tinyframework.proxy;

import org.junit.Test;

public class TestTinyProxySeletingServiceClient {

	TinyProxySeletingServiceClient client;
	
	@Test
	public void testClient() {
		client = TinyProxySeletingServiceClient.getInstance();
		TinyProxySeletingService proxySelectingService = client.getProxySelectingService(TinyProxySeletingService.class);
		TinyProxy proxy = proxySelectingService.select();
	}
}
