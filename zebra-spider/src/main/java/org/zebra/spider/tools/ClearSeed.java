package org.zebra.spider.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zebra.common.domain.dao.*;

public class ClearSeed {
    protected Logger logger = LoggerFactory.getLogger(getClass().getName());
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
