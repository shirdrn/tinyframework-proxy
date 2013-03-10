package org.shirdrn.tinyframework.proxy;

import org.shirdrn.tinyframework.proxy.conf.Configured;
import org.shirdrn.tinyframework.proxy.conf.ReadableContext;
import org.shirdrn.tinyframework.proxy.detector.DefaultProxyDetector;

/**
 * A proxy factory is responsible for managing proxy related 
 * resources and logic implementation about selecting a 
 * possibly available proxy.  
 * 
 * @author Yanjun
 */
public abstract class TinyProxyFactory extends Configured implements TinyProxySelectingService {

	protected TinyProxyMode proxyMode = TinyProxyMode.INVOKE;
	protected TinyProxyDetector proxyDetector;
	
	@Override
	public void setReadableContext(ReadableContext readableContext) {
		super.setReadableContext(readableContext);
		proxyDetector = new DefaultProxyDetector(this);
	}
	
	/**
	 * Release this {@link TinyProxyFactory} instance related resources.
	 * Such as: an opened file, an opened database connection, etc.
	 */
	public abstract void close();
	
}
