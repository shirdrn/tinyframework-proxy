package org.shirdrn.tinyframework.proxy.database.service.impl;

import java.util.List;

import org.shirdrn.tinyframework.proxy.common.CommonDatabaseService;
import org.shirdrn.tinyframework.proxy.database.dao.ProxyDatumDAO;
import org.shirdrn.tinyframework.proxy.database.model.ProxyDatum;
import org.shirdrn.tinyframework.proxy.database.model.ProxyDatumExample;
import org.shirdrn.tinyframework.proxy.database.service.ProxyDatumService;

public class ProxyDatumServiceImpl extends CommonDatabaseService<ProxyDatumDAO> implements
		ProxyDatumService {

	public ProxyDatumServiceImpl() {
		super(ProxyDatumDAO.class);
	}

	@Override
	public List<ProxyDatum> selectByExample(ProxyDatumExample example) {
		// TODO Auto-generated method stub
		return dao.selectByExample(example);
	}

	@Override
	public int updateByPrimaryKey(ProxyDatum record) {
		// TODO Auto-generated method stub
		return dao.updateByPrimaryKey(record);
		
	}
	
	
}
