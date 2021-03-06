package org.shirdrn.tinyframework.proxy;

/**
 * A proxy object, which encapsulates basic proxy related
 * information, such as host name, port, and so on.
 * 
 * @author Yanjun
 */
public class TinyProxy {
	
	protected String host;
	protected int port;
	protected String user;
	protected String password;
	
	public TinyProxy() {
		super();
	}
	
	public TinyProxy(String uri) {
		super();
		String[] a = uri.split(":");
		if(a.length <= 2) {
			this.host = a[0].trim();
			try {
				this.port = Integer.parseInt(a[1].trim());
			} catch (Exception e) {
				this.port = 80;
			}
		}
	}
	
	public TinyProxy(String host, int port) {
		super();
		this.host = host;
		this.port = port;
	}
	
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	@Override
	public boolean equals(Object obj) {
		TinyProxy other = (TinyProxy) obj;
		return this.host.equals(other.host) && this.port==other.port;
	}
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb
		.append("host=" + host + ",")
		.append("port=" + port);
		return sb.toString();
	}
}
