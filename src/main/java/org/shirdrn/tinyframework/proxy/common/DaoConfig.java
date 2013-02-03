package org.shirdrn.tinyframework.proxy.common;

import java.io.IOException;
import java.io.Reader;
import java.util.Properties;

import com.ibatis.common.resources.Resources;
import com.ibatis.dao.client.DaoManager;
import com.ibatis.dao.client.DaoManagerBuilder;

public class DaoConfig {

	private static final String resourceStaging = "org/shirdrn/tinyframework/proxy/database/dao/dao.xml";
	private static DaoManager daoManager = null;
	static {
		try {
			daoManager = newDaoManager(null);
		} catch (Exception e) {
			e.printStackTrace();
			Runtime.getRuntime().exit(-1);
		}
	}

	/**
	 * get a DaoManager instance for database
	 */
	public static DaoManager getDaoManager() {
		return daoManager;
	}

	private static DaoManager newDaoManager(Properties props) throws IOException {
		Reader reader = Resources.getResourceAsReader(resourceStaging);
		return DaoManagerBuilder.buildDaoManager(reader, props);
	}

}
