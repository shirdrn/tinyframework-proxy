package org.shirdrn.tinyframework.commons.proxy.checker;

import org.apache.commons.net.telnet.TelnetClient;
import org.shirdrn.tinyframework.commons.proxy.Proxy;
import org.shirdrn.tinyframework.commons.proxy.ProxyChecker;
import org.shirdrn.tinyframework.commons.proxy.factory.ProxyFactory;

public class TelnetProxyChecker extends ProxyChecker {

	private static TelnetClient telnetClient = new TelnetClient();
	private int connectTimeout = 1000;
	private int socketTimeout = 2000;
	
	public TelnetProxyChecker(ProxyFactory proxyFactory) {
		super(proxyFactory);
		// telnet timeout
		connectTimeout = proxyFactory.getReadableContext().getInt("commons.proxy.telnet.checker.connect.timeout", 1000);
		socketTimeout = proxyFactory.getReadableContext().getInt("commons.proxy.telnet.checker.socket.timeout", 1000);
	}
	
	@Override
	public boolean check(Proxy proxy) {
		return check(proxy.getHost(), proxy.getPort());
	}

	@Override
	public boolean check(String host, int port) {
		boolean connected = false;
		try {
			telnetClient.setConnectTimeout(connectTimeout);
			telnetClient.connect(host, port);
			telnetClient.setSoTimeout(socketTimeout);
			telnetClient.getOutputStream();
			telnetClient.disconnect();
			connected = true;
		} catch (Exception e) { }
		return connected;
	}

}
