package org.zebra.common;

import java.util.Properties;

public class Configuration {
    // basic configuration item
    public static final String PATH_FETCHER_SHOW404 = "http.fetcher.show404page";
    public static final String PATH_FETCHER_IGNOREBINARY = "http.fetcher.ignoreBinary";
    public static final String PATH_FETCHER_USERAGENT = "http.fetcher.userAgent";
    public static final String PATH_FETCHER_ENABLEHTTPS = "http.fetcher.enableHTTPS";
    public static final String PATH_FETCHER_SOCKET_TIMEOUT = "http.fetcher.socketTimeout";
    public static final String PATH_FETCHER_CONNECTION_TIMEOUT = "http.fetcher.connectionTimeout";
    public static final String PATH_FETCHER_MAXCONN_PERHOST = "http.fetcher.maxConnectionPerHost";
    public static final String PATH_FETCHER_MAXDOWNLOAD_SIZE = "http.fetcher.maxDownloadSize";

	private static Properties prop = new Properties();

	static {
		try {
		    String coreProperties = System.getProperty("core.properties");
		    if (null == coreProperties || coreProperties.isEmpty()) {
		        coreProperties = "core.properties";
		    }
			prop.load(Configuration.class.getClassLoader().getResourceAsStream(coreProperties));
		} catch (Exception e) {
			prop = null;
			System.err.println("WARNING: Could not find core.properties file in class path. All default values used.");
		}
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
