package org.shirdrn.tinyframework.commons.proxy;

/**
 * Proxy mode, the following modes are supported:
 * <pre>
 * 1. <tt>INVOKE</tt> 
 * invoke a provided method, and proxy is checked before returning a 
 * available one.
 * 2. <tt>SERVER</tt> 
 * besides invoking a provided method, the proxy module also can check 
 * itself based on a server thread and doesn't depend on callers.
 * </pre>
 * 
 * @author Yanjun
 */
public enum ProxyMode {

	INVOKE(0), 
	SERVER(1); 
	
	private final int code;
	private static final int FIRST_CODE = values()[0].code;
	
	public int getCode() {
		return code;
	}
	
	private ProxyMode(int code) {
		this.code = code;
	}
	
	public static ProxyMode valueOf(int code) {
		final int current = (code & 0xff) - FIRST_CODE;
		return current < 0 || current >= values().length ? null : values()[current];
	}
	
}
