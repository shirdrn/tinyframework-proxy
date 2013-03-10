package org.shirdrn.tinyframework.proxy.locality;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.shirdrn.tinyframework.proxy.TinyProxy;
import org.shirdrn.tinyframework.proxy.TinyProxyDetector;
import org.shirdrn.tinyframework.proxy.TinyProxyFactory;
import org.shirdrn.tinyframework.proxy.TinyProxyMode;
import org.shirdrn.tinyframework.proxy.conf.ReadableContext;
import org.shirdrn.tinyframework.proxy.maxmind.api.Country;
import org.shirdrn.tinyframework.proxy.maxmind.api.LookupService;
import org.shirdrn.tinyframework.proxy.utils.FileUtils;
import org.shirdrn.tinyframework.proxy.utils.ObjectFactory;

/**
 * <dl>
 * Locality proxy factory, use which a local available proxy can be chose.
 * A target user invokes the method the implementation provided, and can get
 * a proxy belonging to the country requested. 
 * </dl><dl>
 * If no proxies belong to the contry, we can return a proxy in other countries
 * belonging to the same continent. If still no proxies are satisfied, at least
 * we could give a proxy randomly selected.
 * </dl>
 * 
 * @author Yanjun
 */
public abstract class LocalityProxyFactory extends TinyProxyFactory implements LocalityProxySelectingService {

	private static final Log LOG = LogFactory.getLog(LocalityProxyFactory.class);
	protected static Map<String, List<String>> CONTINENT_TO_COUNTRIES = new HashMap<String, List<String>>();
	protected static Map<String, String> COUNTRY_TO_CONTINENT = new HashMap<String, String>();
	
	protected File conf;
	protected File geoipFile;
	protected LookupService lookupService;
	
	/** Mapping: country code<=>proxy list*/
	protected Map<String, Selector> proxies = new HashMap<String, Selector>();
	/** Mapping: continent<=>country code list*/
	protected Map<String, List<String>> continents = new HashMap<String, List<String>>();
	
	@Override
	public void setReadableContext(ReadableContext readableContext) {
		super.setReadableContext(readableContext);
		// configure proxy checker instance
		String proxyDetectorClassName = readableContext.get("tiny.proxy.detector.class", 
				"org.shirdrn.tinyframework.proxy.detector.TelnetProxyDetector");
		proxyDetector = ObjectFactory.getInstance(proxyDetectorClassName, TinyProxyDetector.class, this.getClass().getClassLoader(), this);
		LOG.info("Proxy checker implenentation;class=" + proxyDetectorClassName);
		
		// initialize GeoIP lookup service
		// use default implementation 'LookupService' provided by 'maxmind'
		String geoip = readableContext.get("tiny.proxy.locality.geoip.file");
		if(geoip.startsWith("/")) {
			geoipFile = new File(geoip);
		} else {
			geoipFile = new File(geoip);
		}
		if(!geoipFile.exists()) {
			LOG.warn("GeoIP file does not exist;file=" + geoipFile);
			geoipFile = new File(geoip);
			LOG.warn("Use default GeoIP file;file=" + geoipFile);
		}
		LOG.info("GeoIP location;file=" + geoipFile.getAbsolutePath());
		
		try {
			lookupService = new LookupService(geoipFile, LookupService.GEOIP_MEMORY_CACHE);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// load continents and country codes
		LOG.info("Loading continents and country codes...");
		loadContinents();
		LOG.info("Continents and country codes are loaded!");
		
		// load proxies
		LOG.info("Loading proxy data...");
		loadProxies();
		LOG.info("Proxy data is loaded!");
		
		// check proxy mode
		int code = readableContext.getInt("tiny.proxy.factory.mode.code", TinyProxyMode.INVOKE.getCode());
		this.proxyMode = TinyProxyMode.valueOf(code);
		LOG.info("Proxy factory mode;mode=" + this.proxyMode + "[" + code + "]");
	}
	
	/**
	 * Build mappings between continent and country codes.
	 */
	private void loadContinents() {
		String continentDirName = readableContext.get("tiny.proxy.factory.continents.dir", "continents");
		File continentDir = new File(conf, continentDirName);
		
		if(!continentDir.exists()) {
			LOG.warn("Continent dir does not exist;dir=" + continentDir.getAbsolutePath());
			continentDir = new File(continentDirName);
			LOG.warn("Use default continent dir;dir=" + continentDir.getAbsolutePath());
		}
		
		File[] files = continentDir.listFiles();
		for(File file : files) {
			String continent = file.getName();
			Collection<String> lines = FileUtils.populateListWithLines(file, "UTF-8");
			for(Iterator<String> iter = lines.iterator(); iter.hasNext();) {
				String countryCode = iter.next().trim();
				// mapping a continent to country codes
				List<String> countryCodes = CONTINENT_TO_COUNTRIES.get(continent);
				if(countryCodes==null) {
					countryCodes = new ArrayList<String>(1);
					CONTINENT_TO_COUNTRIES.put(continent, countryCodes);
				}
				if(!countryCodes.contains(countryCode)) {
					countryCodes.add(countryCode);
					LOG.info("Read continent;continent=" + continent + ",countryCode=" + countryCode);
				}
				// mapping a country code to a continent
				COUNTRY_TO_CONTINENT.put(countryCode, continent);
			}
		}
		LOG.info("Total continents and countries:");
		Iterator<Map.Entry<String,List<String>>> iter = CONTINENT_TO_COUNTRIES.entrySet().iterator();
		while(iter.hasNext()) {
			Map.Entry<String,List<String>> entry = iter.next();
			LOG.info("continent=" + entry.getKey() + ",countryCount=" + entry.getValue().size());
		}
	}

	/**
	 * Load proxies from given data sources.
	 */
	protected abstract void loadProxies();

	Random random = new Random();
	
	@Override
	public TinyProxy selectByHost(String host) {
		Country country = lookupService.getCountry(host);
		return selectByCountry(country.getCode());
	}
	
	@Override
	public TinyProxy select() {
		if(proxies.isEmpty()) {
			LOG.warn("No proxy data loaded!");
			return null;
		}
		TinyProxy proxy = null;
		int which = random.nextInt(proxies.size());
		Iterator<Entry<String, Selector>> iter = proxies.entrySet().iterator();
		int counter = 0;
		while(iter.hasNext()) {
			if(counter!=which) {
				iter.next();
				++counter;
			} else {
				proxy = selectOne(proxies.get(iter.next().getKey()));
				break;
			}
		}
		return proxy;
	}

	@Override
	public TinyProxy selectByCountry(String countryCode) {
		TinyProxy proxy = null;
		Selector selector = proxies.get(countryCode);
		if(selector==null) {
			int maxSelectingTimes = 3;
			int counter = 0;
			// no proxies in the country, choose a proxy in the same continent
			while(true) {
				if(!countryCode.equals("--")) {
					String theContinent = COUNTRY_TO_CONTINENT.get(countryCode);
					List<String> countries = continents.get(theContinent);
					if(countries!=null) {
						int whichCountry = random.nextInt(countries.size());
						String theCountryCode = countries.get(whichCountry);
						selector = proxies.get(theCountryCode);
					}
				}
				if(selector!=null || ++counter>=maxSelectingTimes) {
					if(selector!=null) {
						proxy = selectOne(selector);
					}
					break;
				}
			}
		} else {
			proxy = selectOne(selector);
			LOG.debug("selectByCountry(" + countryCode + ")::ProxyList[" + selector.selectedProxyList + "]");
		}
		LOG.debug("selectByCountry(" + countryCode + ")::Proxy[" + proxy + "]");
		// finally, no proxy selected, then select randomly
		if(proxy==null) {
			proxy = select();
			LOG.debug("select(" + countryCode + ")::Proxy[" + proxy + "]");
		}
		return proxy;
	}
	
	/**
	 * Select a proxy based on RR(Round-Robin) policy
	 * @param selector
	 * @return
	 */
	protected TinyProxy selectOne(Selector selector) {
		TinyProxy proxy = null;
		synchronized(selector.currentIndex) {
			int counter = 0;
			int index = selector.currentIndex;
			LOG.debug("Enter::selector.currentIndex=" + index + ",size=" + selector.selectedProxyList.size());
			while(counter<selector.selectedProxyList.size()) {
				proxy = selector.selectedProxyList.get(index);
				boolean available = proxyDetector.detect(proxy);
				if(++index>=selector.selectedProxyList.size()) {
					index = 0;
				}
				if(!available) {
					counter++;
				} else {
					break;
				}
			}
			LOG.debug("Exit::selector.currentIndex=" + index + ",size=" + selector.selectedProxyList.size());
			if(++index>=selector.selectedProxyList.size()) {
				index = 0;
			}
			selector.currentIndex = index;
		}
		return proxy;
	}
	
	/**
	 * Holding a proxy list, in which all proxies belong to the country.
	 * 
	 * @author Yanjun
	 */
	protected class Selector {
		
		String countryCode;
		List<TinyProxy> selectedProxyList;
		Integer currentIndex = 0;
		
		@Override
		public String toString() {
			return selectedProxyList.toString();
		}
		
	}

	@Override
	public void close() {
		if(lookupService!=null) {
			lookupService.close();
		}
	}

}
