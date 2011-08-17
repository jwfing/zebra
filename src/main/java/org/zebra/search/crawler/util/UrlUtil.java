package org.zebra.search.crawler.util;

import java.net.MalformedURLException;
import java.net.URL;

public final class UrlUtil {

	public static String getAbsoluteUrl(String parentUrl, String relativeUrl) {
		// TODO: want fix
		return parentUrl + relativeUrl;
	}
	public static String getHostFromUrl(String urlStr) {
		try {
			URL url = new URL(urlStr);
			return url.getHost();
		} catch (Exception ex) {
			return "";
		}
	}
	public static String getAbsoluteUrl(URL parentUrl, String relativeUrl) {
		// TODO: want fix
		if (null == relativeUrl || relativeUrl.isEmpty()) {
			return "";
		}
		if (relativeUrl.startsWith("/") || relativeUrl.startsWith(".")) {
			return parentUrl + relativeUrl;
		}
		return relativeUrl;
	}
	public static String getCanonicalURL(String url) {
		URL canonicalURL = getCanonicalURL(url, null);
		if (canonicalURL != null) {
			return canonicalURL.toExternalForm();
		}
		return null;
	}

	public static URL genURL(String url) {
		URL parentUrl = null;
		try {
			parentUrl = new URL(url);
		} catch (MalformedURLException e1) {
			return null;
		}
		return parentUrl;
	}

	public static URL getCanonicalURL(String href, String context) {
		if (href.contains("#")) {
            href = href.substring(0, href.indexOf("#"));
        }
		href = href.replace(" ", "%20");
        try {
        	URL canonicalURL;
        	if (context == null) {
        		canonicalURL = new URL(href);
        	} else {
        		canonicalURL = new URL(new URL(context), href);
        	}
        	String path = canonicalURL.getPath();
        	if (path.startsWith("/../")) {
        		path = path.substring(3);
        		canonicalURL = new URL(canonicalURL.getProtocol(), canonicalURL.getHost(), canonicalURL.getPort(), path);
        	} else if (path.contains("..")) {
        		System.out.println(path);
        	}
        	return canonicalURL;
        } catch (MalformedURLException ex) {
            return null;
        }
	}
}
