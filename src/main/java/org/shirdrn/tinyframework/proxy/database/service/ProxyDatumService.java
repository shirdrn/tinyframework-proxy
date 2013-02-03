package org.shirdrn.tinyframework.proxy.database.service;

import java.util.List;

import org.shirdrn.tinyframework.proxy.database.model.ProxyDatum;
import org.shirdrn.tinyframework.proxy.database.model.ProxyDatumExample;

public interface ProxyDatumService {

	List<ProxyDatum> selectByExample(ProxyDatumExample example);

	int updateByPrimaryKey(ProxyDatum proxyDatum);

}
