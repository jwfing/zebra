package org.zebra.search.crawler.tool;

import org.apache.log4j.Logger;
import org.zebra.search.crawler.urlPool.*;

public class ClearSeed {
	private static final Logger logger = Logger.getLogger(ClearSeed.class);
	private UrlAppender appender = UrlAppender.getInstance();
	public ClearSeed(UrlStorage storage) {
		this.appender.setStorage(storage);
	}
	public int execute(int level) {
		logger.info("drop repeat seed with level=" + level);
		this.appender.dropAllUrls(level, Constants.UrlType.REPEAT);
		return 0;
	}
}
