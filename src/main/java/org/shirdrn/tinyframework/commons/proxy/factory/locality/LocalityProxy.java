package org.shirdrn.tinyframework.commons.proxy.factory.locality;

import org.shirdrn.tinyframework.commons.proxy.Proxy;

/**
 * In order to gain locality features and benefits, the proxy
 * object has to contain much more locality related information 
 * to be used, for example, country codes, continents, etc.
 * 
 * @author Yanjun
 */
public class LocalityProxy extends Proxy {

	protected String countryCode;
	protected String continent;
	
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
