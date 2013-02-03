package org.shirdrn.tinyframework.proxy.detector;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.SocketAddress;
import java.net.URL;
import java.util.Random;

import org.apache.log4j.Logger;
import org.shirdrn.tinyframework.proxy.TinyProxy;
import org.shirdrn.tinyframework.proxy.TinyProxyDetector;
import org.shirdrn.tinyframework.proxy.TinyProxyFactory;

/**
 * Start a HTTP conversation for a proxy object, and check whether
 * the response code is a successful status.
 * 
 * @author Yanjun
 */
public class HttpConversationProxyDetector extends TinyProxyDetector {

	private static final Logger LOG = Logger.getLogger(HttpConversationProxyDetector.class);
	private int connectTimeout;
	private String[] sampleLinks = new String[] { 
			"http://www.dmoz.org/", 
			"http://www.apache.org/"
			};
	private Random random = new Random();
	
	public HttpConversationProxyDetector(TinyProxyFactory proxyFactory) {
		super(proxyFactory);
		connectTimeout = proxyFactory.getReadableContext().getInt("commons.proxy.http.checker.connect.timeout", 3000);
		sampleLinks = proxyFactory.getReadableContext().getStrings("commons.proxy.http.checker.sample.links", sampleLinks);
		LOG.info("Configured test links;sampleLinks=" + sampleLinks);
	}

	@Override
	public boolean detect(TinyProxy proxy) {
		return detect(proxy.getHost(), proxy.getPort());
	}

	@Override
	public boolean detect(String host, int port) {
		int responseCode = 0;
		try {
			int which = random.nextInt(sampleLinks.length);
			String link = sampleLinks[which];
			SocketAddress addr = new InetSocketAddress(host, port);
			java.net.Proxy httpProxy = new java.net.Proxy(java.net.Proxy.Type.HTTP, addr);
			responseCode = crawl(link, httpProxy);
		} catch (Exception e) {
			LOG.warn("Proxy status;host=" + host + ",port=" + port + ",exception=" + e.getMessage());
		}
		return responseCode==200;
	}
	
	private int crawl(String link, java.net.Proxy httpProxy) throws IOException {
		int responseCode = 0;
		HttpURLConnection conn = null;
		try {
			URL u = new URL(link);
			conn = (HttpURLConnection) u.openConnection(httpProxy);
			conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.1.6) Gecko/20091201 Firefox/3.5.6");
			conn.setRequestProperty("Accept-Language", "en-us,en;q=0.7,zh-cn;q=0.3");
			conn.setRequestProperty("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.7");
			conn.setRequestProperty("Keep-Alive", "300");
			conn.setRequestProperty("Connection", "keep-alive");
			conn.setRequestProperty("If-Modified-Since", "Fri, 02 Jan 2009 17:00:05 GMT");
			conn.setRequestProperty("If-None-Match", "\"1261d8-4290-df64d224\"");
			conn.setRequestProperty("Cache-Control", "max-age=0");
			conn.setConnectTimeout(connectTimeout);
			HttpURLConnection.setFollowRedirects(true);
			responseCode = conn.getResponseCode();
		} catch (MalformedURLException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		} finally {
			if(conn!=null) {
				conn.disconnect();
			}
		}
		return responseCode;
	}

}
