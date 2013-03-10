package org.shirdrn.tinyframework.proxy.locality;

import org.shirdrn.tinyframework.proxy.TinyProxy;

/**
 * In order to gain locality features and benefits, the proxy
 * object has to contain much more locality related information 
 * to be used, for example, country codes, continents, etc.
 * 
 * @author Yanjun
 */
public class LocalityProxy extends TinyProxy {

	protected String countryCode;
	protected String continent;
	
	public LocalityProxy() {
		super();
	}
	
	public LocalityProxy(String host, int port) {
		super(host, port);
	}
	
	public LocalityProxy(String uri) {
		super(uri);
	}
	
	public LocalityProxy(String host, int port, String countryCode, String continent) {
		super(host, port);
		this.countryCode = countryCode;
		this.continent = continent;
	}
	
	public LocalityProxy(String uri, String countryCode, String continent) {
		super(uri);
		this.countryCode = countryCode;
		this.continent = continent;
	}
	
	public String getCountryCode() {
		return countryCode;
	}
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	public String getContinent() {
		return continent;
	}
	public void setContinent(String continent) {
		this.continent = continent;
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb
		.append(super.toString())
		.append(",countryCode=" + countryCode)
		.append(",continent=" + continent);
		return sb.toString();
	}
	
}
