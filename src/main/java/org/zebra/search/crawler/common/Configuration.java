package org.zebra.search.crawler.common;

import java.util.Properties;

import org.apache.log4j.Logger;

public class Configuration {
	public static final String PATH_ALLOC_SCAN_NUM = "crawler.allocator.scans";
	public static final String PATH_ALLOC_SCAN_THREAD = "crawler.allocator.scan.";
	public static final String PATH_ALLOC_SCAN_ONCE_INTERVAL = "crawler.allocator.once.interval";

	public static final String PATH_FETCHER_NUM = "crawler.fetcher.num";
	public static final String PATH_FETCHER_SHOW404 = "crawler.fetcher.show404page";
	public static final String PATH_FETCHER_IGNOREBINARY = "crawler.fetcher.ignoreBinary";
	public static final String PATH_FETCHER_USERAGENT = "crawler.fetcher.userAgent";
	public static final String PATH_FETCHER_ENABLEHTTPS = "crawler.fetcher.enableHTTPS";
	public static final String PATH_FETCHER_SOCKET_TIMEOUT = "crawler.fetcher.socketTimeout";
	public static final String PATH_FETCHER_CONNECTION_TIMEOUT = "crawler.fetcher.connectionTimeout";
	public static final String PATH_FETCHER_MAXCONN_PERHOST = "crawler.fetcher.maxConnectionPerHost";
	public static final String PATH_FETCHER_MAXCONN_TOTAL = "crawler.fetcher.totalConnection";

	public static final String PATH_URLPOOL_DIR = "crawler.urlpool.dir";
	public static final String PATH_URLPOOL_RESUME = "crawler.urlpool.resume";

	public static final String PATH_PIPELINE_CONF = "crawler.processor.conf";
	public static final String PATH_PIPELINE_THREADS = "crawler.processor.threads";

	public static final String CONST_LEVEL_SUFFIX = ".level";
	public static final String CONST_INTERVAL_SUFFIX = ".interval";

	private static Properties prop = new Properties();

	static {
		try {
			prop.load(Configuration.class.getClassLoader()
					.getResourceAsStream("./zebra_crawler.properties"));
		} catch (Exception e) {
			prop = null;
			System.err.println("WARNING: Could not find ./zebra_crawler.properties file in class path. I will use the default values.");
		}
	}
	
	public boolean initialize(String filename) {
		return true;
	}
	
	public static String getStringProperty(String key, String defaultValue) {
		if (prop == null || prop.getProperty(key) == null) {
			return defaultValue;
		}
		return prop.getProperty(key);
	}

	public static int getIntProperty(String key, int defaultValue) {
		if (prop == null || prop.getProperty(key) == null) {
			return defaultValue;
		}
		return Integer.parseInt(prop.getProperty(key));
	}
	
	public static short getShortProperty(String key, short defaultValue) {
		if (prop == null || prop.getProperty(key) == null) {
			return defaultValue;
		}
		return Short.parseShort(prop.getProperty(key));
	}

	public static long getLongProperty(String key, long defaultValue) {
		if (prop == null || prop.getProperty(key) == null) {
			return defaultValue;
		}
		return Long.parseLong(prop.getProperty(key));
	}
	
	public static boolean getBooleanProperty(String key, boolean defaultValue) {
		if (prop == null || prop.getProperty(key) == null) {
			return defaultValue;
		}
		return prop.getProperty(key).toLowerCase().trim().equals("true");
	}
}
