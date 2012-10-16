package org.zebra.spider.tools;

import org.apache.log4j.Logger;
import org.zebra.common.domain.dao.*;

public class ClearSeed {
	private static final Logger logger = Logger.getLogger(ClearSeed.class);
	private SeedDao dao;
	public ClearSeed(SeedDao dao) {
	    this.dao = dao;
	}
	public void setSeedDao(SeedDao dao) {
	    this.dao = dao;
	}
	public int execute() {
		logger.info("clear");
		return 0;
	}
}
