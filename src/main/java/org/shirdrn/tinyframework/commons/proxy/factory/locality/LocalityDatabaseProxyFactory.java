package org.shirdrn.tinyframework.commons.proxy.factory.locality;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.shirdrn.tinyframework.commons.core.conf.ReadableContext;
import org.shirdrn.tinyframework.commons.core.utils.DatetimeUtils;
import org.shirdrn.tinyframework.commons.proxy.Proxy;
import org.shirdrn.tinyframework.commons.proxy.ProxyMode;
import org.shirdrn.tinyframework.commons.proxy.ProxyStatus;
import org.shirdrn.tinyframework.commons.proxy.maxmind.api.Country;

/**
 * Locality proxy factory, use which a local available proxy can be chose.
 * A target user invokes the method the implementation provided, and can get
 * a proxy belonging to the country requested. 
 * <dl>
 * If no proxies belong to the country, we can return a proxy in other countries
 * belonging to the same continent. If still no proxies are satisfied, at least
 * we could give a proxy randomly selected.
 * </dl><dl>
 * This implementation reads proxy data from a database, and the default query
 * conditions are: successCount>0 in table 'traffic_proxy'. If you want to improve
 * the query result, change the query condition is a better way.
 * </dl>
 * 
 * @author Yanjun
 */
public class LocalityDatabaseProxyFactory extends LocalityProxyFactory {

	private static final Log LOG = LogFactory.getLog(LocalityDatabaseProxyFactory.class);
	private static final TrafficProxyService proxyService = new TrafficProxyServiceImpl();
	private Thread proxyCheckerThread;
	
	@Override
	public void setReadableContext(ReadableContext readableContext) {
		super.setReadableContext(readableContext);
		// initialize GeoIP lookup service
		// use AdvancedLookupService implementation we improved
		try {
			lookupService = new AdvancedLookupService(geoipFile, LookupService.GEOIP_MEMORY_CACHE);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(proxyMode==ProxyMode.SERVER) {
			proxyCheckerThread = new Thread(new ProxyCheckerThread());
			proxyCheckerThread.start();
		}
	}
	
	@Override
	protected void loadProxies() {
		TrafficProxyExample example = new TrafficProxyExample();
		example.createCriteria().andStatusEqualTo(ProxyStatus.AVAILABLE);
		List<TrafficProxy>  prxs = proxyService.query(example);
		for(TrafficProxy prx : prxs) {
			doLine(prx);
		}
		debugProxiesStat(proxies);
	}

	private void debugProxiesStat(Map<String, Selector> prxes) {
		if(LOG.isDebugEnabled()) {
			LOG.debug("Proxy statistics;count=" + prxes.size());
			Iterator<Entry<String, Selector>> iter = prxes.entrySet().iterator();
			while(iter.hasNext()) {
				Entry<String, Selector> entry = iter.next();
				LOG.debug("Proxy statistics;country=" + entry.getKey() + ",count=" + entry.getValue().selectedProxyList.size() + ",selector=" + entry.getValue());
			}
		}
	}

	private void doLine(TrafficProxy prx) {
		// if no country code, default set to ("--", "N/A")
		populate(proxies, prx.getHost(), prx.getPort());
	}
	
	private void populate(Map<String, Selector> proxyMap, String host, int port) {
		LocalityProxy proxy = new LocalityProxy();
		proxy.setHost(host);
		proxy.setPort(port);
		Country country = lookupService.getCountry(host);
		proxy.setCountryCode(country.getCode());
		String continent = COUNTRY_TO_CONTINENT.get(country.getCode());
		proxy.setContinent(continent);
		if(!country.getCode().equals("--")) {
			Selector selector = proxyMap.get(proxy.getCountryCode());
			if(selector==null) {
				selector = new Selector();
				selector.countryCode = proxy.getCountryCode();
				selector.selectedProxyList = Collections.synchronizedList(new ArrayList<Proxy>());;
				proxyMap.put(proxy.getCountryCode(), selector);
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
	
	private AtomicInteger invokeCounter = new AtomicInteger(0);
	private Boolean waitingLock = new Boolean(false);
	
	/**
	 * Check proxy in database, we can configure the time interval
	 * for checking existed proxies
	 * 
	 * @author Yanjun
	 */
	class ProxyCheckerThread implements Runnable {

		@Override
		public void run() {
			int checkInterval = readableContext.getInt("commons.proxy.check.interval", 21600000);
			LOG.debug("Read check interval;checkInterval=" + checkInterval);
			while(true) {
				try {
					// sleep, default 6h
					Thread.sleep(checkInterval);
					LOG.info("START to check and update proies;current=" + DatetimeUtils.formatDateTime(new Date(), "yyyy-MM-dd HH-mm-SS"));
					Map<String, Selector> tempProxies = checkProxies();
					// lock
					waitingLock = true;
					// wait all invokers entered to return
					LOG.debug("Enter: invokeCounter=" + invokeCounter);
					while(invokeCounter.get()!=0) {
						Thread.sleep(15);
					}
					// update proxies in memory
					proxies = tempProxies;
					// unlock
					waitingLock = false;
					debugProxiesStat(tempProxies);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					LOG.info("FINISH to check and update proies;current=" + DatetimeUtils.formatDateTime(new Date(), "yyyy-MM-dd HH-mm-SS"));
				}
			}
		}
		
		/**
		 * Check the availability of proxies in database.
		 */
		public Map<String, Selector> checkProxies() {
			Map<String, Selector> tempProxies = new HashMap<String, Selector>();
			// check proxy
			LOG.info("Start to check proxies in database...");
			try {
				TrafficProxyExample example = new TrafficProxyExample();
				List<TrafficProxy>  list = proxyService.query(example);
				for(TrafficProxy trafficProxy : list) {
					boolean isAvailable = proxyChecker.check(trafficProxy.getHost(), trafficProxy.getPort());
					if(isAvailable) {
						trafficProxy.setStatus(ProxyStatus.AVAILABLE);
					} else {
						trafficProxy.setStatus(ProxyStatus.UNAVAILABLE);
					}
					updateStatus(trafficProxy);
					// prepare to update proxies in memory
					if(isAvailable) {
						populate(tempProxies, trafficProxy.getHost(), trafficProxy.getPort());
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return tempProxies;
		}

		private void updateStatus(TrafficProxy trafficProxy) {
			try {
				Date date = new Date();
				trafficProxy.setUpdatedAt(date);
				proxyService.update(trafficProxy);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
		
	private void beforeSelect() {
		// only read waitingLock
		while(waitingLock) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		invokeCounter.incrementAndGet();
		LOG.debug("Enter: invokeCounter=" + invokeCounter);
	}
	
	private void afterSelect() {
		invokeCounter.decrementAndGet();
		LOG.debug("Exit: invokeCounter=" + invokeCounter);
	}
	
	@Override
	public Proxy selectByHost(String host) {
		beforeSelect();
		Proxy proxy = super.selectByHost(host);
		afterSelect();
		return proxy;
	}

	@Override
	public Proxy select() {
		beforeSelect();
		Proxy proxy = super.select();
		afterSelect();
		return proxy;
	}

	@Override
	public Proxy selectByCountry(String countryCode) {
		beforeSelect();
		Proxy proxy = super.selectByCountry(countryCode);
		afterSelect();
		return proxy;
	}
	
	@Override
	public void close() {
		super.close();
		if(proxyCheckerThread!=null && proxyCheckerThread.isAlive()) {
			try {
				proxyCheckerThread.interrupt();
			} catch (Exception e) {
				if(LOG.isDebugEnabled()) {
					e.printStackTrace();
				}
			} finally {
				LOG.info("Proxy factory has been closed!");
			}
		}
	}
	
}
