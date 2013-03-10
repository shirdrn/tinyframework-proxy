package org.shirdrn.tinyframework.proxy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.shirdrn.tinyframework.proxy.conf.ReadableContext;
import org.shirdrn.tinyframework.proxy.locality.LocalityProxyFactory;
import org.shirdrn.tinyframework.proxy.utils.ObjectFactory;

/**
 * Default proxy factory implementation. It only supports to select
 * a available proxy randomly, and the following advanced select-like 
 * methods are not supported:
 * <pre>
 * 1.{{@link #selectByCountry(String)} select a proxy by a country code
 * 2.{{@link #selectByHost(String)} select a proxy by a host name
 * </pre>
 * 
 * If the advanced services is what you are wanted greatly, please try to
 * choose the advanced implementations of {@link LocalityProxyFactory}.
 * 
 * @author Yanjun
 */
public class DefaultProxyFactory extends TinyProxyFactory {

	private static final Log LOG = LogFactory.getLog(DefaultProxyFactory.class);
	protected String fileSuffix = ".prx";
	protected Integer currentIndex = 0;
	protected List<TinyProxy> proxies = new ArrayList<TinyProxy>();
	
	@Override
	public void setReadableContext(ReadableContext readableContext) {
		super.setReadableContext(readableContext);
		fileSuffix = readableContext.get("tiny.proxy.file.suffix", ".prx");
		// configure proxy checker instance
		String proxyCheckerClassName = readableContext.get("tiny.proxy.detector.class", 
				"org.shirdrn.tinyframework.proxy.detector.DefaultProxyDetector");
		proxyDetector = ObjectFactory.getInstance(proxyCheckerClassName, 
				TinyProxyDetector.class, this.getClass().getClassLoader(), this);
		// http proxy dir
		String httpProxyDirName = readableContext.get("tiny.proxy.file.dir", "proxies");
		File httpProxyDir = null;
		if(httpProxyDirName.startsWith("/")) {
			httpProxyDir = new File(httpProxyDirName);
		} else {
			httpProxyDir = new File(httpProxyDirName);
			LOG.info(httpProxyDir.getAbsolutePath());
		}
		// load proxies
		loadProxies(httpProxyDir);
	}

	private void loadProxies(File httpProxyDir) {
		File[] files = httpProxyDir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(fileSuffix);
			}
		});
		if(files == null) {
			LOG.warn("Can not load proxy files!");
			return;
		}
		for(File file : files) {
			LOG.info("Load proxy from file;file=" + file);
			FileInputStream fis = null;
			BufferedReader reader = null;
			try {
				fis = new FileInputStream(file.getAbsolutePath());
				reader = new BufferedReader(new InputStreamReader(fis, Charset.defaultCharset()));
				String line = null;
				while((line=reader.readLine())!=null) {
					try {
						line = line.trim();
						if(!line.isEmpty() && !line.startsWith("#") && line.indexOf(":")!=-1) {
							String[] a = line.split(":");
							TinyProxy proxy = new TinyProxy();
							proxy.setHost(a[0].trim());
							proxy.setPort(Integer.parseInt(a[1].trim()));
							LOG.info("Read proxy;proxy=[" + proxy.toString() + "]");
							proxies.add(proxy);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if(fis!=null) {
						fis.close();
					}
					if(reader!=null) {
						reader.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		
		}
	}
	
	@Override
	public TinyProxy select() {
		TinyProxy proxy = null;
		synchronized(currentIndex) {
			int counter = 0;
			int index = currentIndex;
			while(counter<proxies.size()) {
				proxy = proxies.get(index);
				boolean available = proxyDetector.detect(proxy);
				if(!available) {
					counter++;
				} else {
					index++;
					break;
				}
				if(++index>proxies.size()) {
					index = 0;
				}
			}
			currentIndex = index;
		}
		return proxy;
	}

	@Override
	public void close() {
		
	}

}
