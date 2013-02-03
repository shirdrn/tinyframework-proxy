package org.shirdrn.tinyframework.proxy.locality;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.shirdrn.tinyframework.proxy.TinyProxy;
import org.shirdrn.tinyframework.proxy.conf.ReadableContext;
import org.shirdrn.tinyframework.proxy.maxmind.api.Country;
import org.shirdrn.tinyframework.proxy.maxmind.api.LookupService;

/**
 * Locality proxy factory, use which a local available proxy can be chose.
 * A target user invokes the method the implementation provided, and can get
 * a proxy belonging to the country requested. 
 * <dl>
 * If no proxies belong to the country, we can return a proxy in other countries
 * belonging to the same continent. If still no proxies are satisfied, at least
 * we could give a proxy randomly selected.
 * </dl><dl>
 * This implementation reads proxy data from a given directory containing some plain 
 * text files whose names are filtered by a specified suffix name configured.
 * </dl>
 * 
 * @author Yanjun
 */
public class LocalityFileProxyFactory extends LocalityProxyFactory {

	private static final Log LOG = LogFactory.getLog(LocalityFileProxyFactory.class);
	protected String proxyFileSuffix = ".prx";
	
	@Override
	public void setReadableContext(ReadableContext readableContext) {
		super.setReadableContext(readableContext);
		proxyFileSuffix = readableContext.get("commons.proxy.file.suffix", ".prx");
		// initialize GeoIP lookup service
		// use AdvancedLookupService implementation we improved
		try {
			lookupService = new AdvancedLookupService(geoipFile, LookupService.GEOIP_MEMORY_CACHE);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Load proxies from given files into memory.
	 */
	@Override
	protected void loadProxies() {
		// HTTP proxy directory
		String httpProxyDirName = readableContext.get("commons.proxy.file.dir", "proxies");
		File httpProxyDir = new File(conf, httpProxyDirName);
		File[] files = httpProxyDir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(proxyFileSuffix);
			}
		});
		for(File file : files) {
			LOG.info("Load proxy from file;file=" + file);
			FileInputStream fis = null;
			BufferedReader reader = null;
			try {
				fis = new FileInputStream(file.getAbsolutePath());
				reader = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
				String line = null;
				while((line=reader.readLine())!=null) {
					try {
						line = line.trim();
						doLine(line);
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

	private void doLine(String line) {
		if(!line.isEmpty() && !line.startsWith("#") && line.indexOf(":")!=-1) {
			String[] a = line.split(":");
			int port = Integer.parseInt(a[1].trim());
			String host = a[0].trim();
			LocalityProxy proxy = new LocalityProxy();
			proxy.setHost(host);
			proxy.setPort(port);
			// if no country code, default set to ("--", "N/A")
			Country country = lookupService.getCountry(host);
			proxy.setCountryCode(country.getCode());
			String continent = COUNTRY_TO_CONTINENT.get(country.getCode());
			proxy.setContinent(continent);
			if(!country.getCode().equals("--")) {
				Selector selector = proxies.get(proxy.getCountryCode());
				if(selector==null) {
					selector = new Selector();
					selector.countryCode = proxy.getCountryCode();
					selector.selectedProxyList = Collections.synchronizedList(new ArrayList<TinyProxy>());;
					proxies.put(proxy.getCountryCode(), selector);
				}
				selector.selectedProxyList.add(proxy);
				// continent to country codes
				List<String> codes = continents.get(continent);
				if(codes==null) {
					codes = new ArrayList<String>();
					continents.put(continent, codes);
				}
				if(!codes.contains(country.getCode())) {
					codes.add(country.getCode());
				}
				LOG.info("Read proxy;proxy=[" + proxy.toString() + "]");
			} else {
				LOG.warn("Unknown proxy;" + proxy);
			}
		}
	}

	@Override
	public void close() {
		super.close();
	}

}
