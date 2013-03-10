package org.shirdrn.tinyframework.proxy.detector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.net.telnet.TelnetClient;
import org.shirdrn.tinyframework.proxy.TinyProxy;
import org.shirdrn.tinyframework.proxy.TinyProxyDetector;
import org.shirdrn.tinyframework.proxy.TinyProxyFactory;

public class TelnetProxyDetector extends TinyProxyDetector {

	private static final Log LOG = LogFactory.getLog(TelnetProxyDetector.class);
	private static TelnetClient telnetClient = new TelnetClient();
	private int connectTimeout = 1000;
	private int socketTimeout = 2000;
	
	public TelnetProxyDetector(TinyProxyFactory proxyFactory) {
		super(proxyFactory);
		// telnet timeout
		connectTimeout = proxyFactory.getReadableContext().getInt("tiny.proxy.telnet.detector.connect.timeout", 1000);
		socketTimeout = proxyFactory.getReadableContext().getInt("tiny.proxy.telnet.detector.socket.timeout", 1000);
		LOG.info("Timeout configuration;connectTimeout=" + connectTimeout + ",socketTimeout=" + socketTimeout);
	}
	
	@Override
	public boolean detect(TinyProxy proxy) {
		return detect(proxy.getHost(), proxy.getPort());
	}

	@Override
	public boolean detect(String host, int port) {
		boolean connected = false;
		try {
			telnetClient.setConnectTimeout(connectTimeout);
			telnetClient.connect(host, port);
			telnetClient.setSoTimeout(socketTimeout);
			telnetClient.getOutputStream();
			telnetClient.disconnect();
			connected = true;
		} catch (Exception e) { }
		
		LOG.debug("Result;available=" + connected + ",host=" + host + ",port=" + port);
		return connected;
	}

}
