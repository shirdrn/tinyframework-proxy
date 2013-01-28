package org.shirdrn.tinyframework.commons.proxy.factory;

import org.shirdrn.tinyframework.commons.core.conf.Configured;
import org.shirdrn.tinyframework.commons.core.conf.ReadableContext;
import org.shirdrn.tinyframework.commons.proxy.ProxyChecker;
import org.shirdrn.tinyframework.commons.proxy.ProxyMode;
import org.shirdrn.tinyframework.commons.proxy.ProxyService;
import org.shirdrn.tinyframework.commons.proxy.checker.DefaultProxyChecker;

/**
 * A proxy factory is responsible for managing proxy related 
 * resources and logic implementation about selecting a 
 * possibly available proxy.  
 * 
 * @author Yanjun
 */
public abstract class ProxyFactory extends Configured implements ProxyService {

	protected ProxyMode proxyMode = ProxyMode.INVOKE;
	protected ProxyChecker proxyChecker;
	
	@Override
	public void setReadableContext(ReadableContext readableContext) {
		super.setReadableContext(readableContext);
		proxyChecker = new DefaultProxyChecker(this);
	}
	
	/**
	 * Release this {@link ProxyFactory} instance related resources.
	 * Such as: an opened file, an opened database connection, etc.
	 */
	public abstract void close();
	
}
