package org.zebra.common.utils;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import org.zebra.common.CrawlDocument;

public class ProcessorUtil {
	private static final Logger logger = Logger.getLogger(ProcessorUtil.class);

	public static final String COMMON_PROP_CONTENTTYPE = "contenttype";
	public static final String COMMON_PROP_ENCODING = "encoding";
	public static final String COMMON_PROP_BASE = "base";
	public static final String COMMON_PROP_NODELIST = "nodelist";
	public static final String COMMON_PROP_OUTLINKS = "outlinks";
	public static final String COMMON_PROP_BINARYLINKS = "binarylinks";
	public static final String COMMON_PROP_ANCHOR = "anchor";
	public static final String COMMON_PROP_SEEDURL = "seedurl";
	public static final String COMMON_PROP_FLAG = "flag";
	public static final String COMMON_PROP_DEPTH = "depth";
	public static final String COMMON_PROP_OLDCONTENT = "oldContent";
	public static final String COMMON_PROP_DISABLEFOLLOW = "disableFollow";
	public static final String COMMON_PROP_CHANNEL = "channel";

	public static final String COMMON_PROP_TITLE = "title";
	public static final String COMMON_PROP_DESCRIPTION = "description";
	public static final String COMMON_PROP_ARTICLETITLE = "articletitle";
	public static final String COMMON_PROP_CRAWLTIME = "crawltime";
	public static final String COMMON_PROP_PUBLISHTIME = "publishtime";
	public static final String COMMON_PROP_MAINBODY = "mainbody";
	public static final String COMMON_PROP_CRAWLSOURCE = "crawlsource";
	public static final String COMMON_PROP_PUBLISHSOURCE = "publishsource";
	public static final String COMMON_PROP_TAG = "tag";

    public static final String FLAG_VALUE_LIST = "list";
	public static final String FLAG_VALUE_CONTENT = "content";
	public static final String FLAG_VALUE_USR1 = "usr1";
    public static final String FLAG_VALUE_USR2 = "usr2";

	public static String getHttpCharset(CrawlDocument doc) {
		String charsetname = null;
		String contenttype = doc.getFeature(COMMON_PROP_CONTENTTYPE);
		if (contenttype != null && !contenttype.isEmpty()) {
			String flagString = "charset=";
			int index = contenttype.indexOf(flagString);
			if (index != -1) {
				int endIndex = contenttype.indexOf(";", index);
				if (endIndex == -1) {
					endIndex = contenttype.length();
				}
				charsetname = contenttype.substring(
						index + flagString.length(), endIndex).trim();
				if (!Charset.isSupported(charsetname)) {
					logger.warn("charset(" + charsetname + ") isn't supported.");
					charsetname = "";
				}
			}
		}
		return charsetname;
	}

	/**
	 * get doc mime type from http response
	 * 
	 * @param doc
	 * @return mime type of doc
	 */
	public static List<String> getDocMimeType(CrawlDocument doc) {
		List<String> docMimeTypes = new ArrayList<String>();

			String contenttype = (String) doc.getFeature(
					COMMON_PROP_CONTENTTYPE);
			if (contenttype != null) {
				String flagString = ";";

				String[] types = contenttype.toLowerCase().split(flagString);
				for (int i = 0; i < types.length; i++) {
					String mimeType = types[i].trim();
					if (mimeType.indexOf("charset=") != -1
							|| mimeType.length() <= 1) {
						continue;
					}

					docMimeTypes.add(mimeType);
				}
			}

		return docMimeTypes;
	}

	/**
	 * get document encoding
	 * 
	 * @param doc
	 * @param defaultEncoding
	 * @return encoding detected
	 */
	public static String getEncoding(CrawlDocument doc, String defaultEncoding) {
		String encoding = (String) doc.getFeature(
				COMMON_PROP_ENCODING);
		if (encoding != null) {
			return encoding;
		} else {
			return defaultEncoding;
		}
	}

	public static String getEncoding(String str) {
		String encode = "GB2312";
		try {
			if (str.equals(new String(str.getBytes(encode), encode))) {
				String s = encode;
				return s;
			}
		} catch (Exception ex) {
			logger.warn("exception encountered @ GB2312, cause:" + ex.getMessage());
		}
		encode = "GBK";
		try {
			if (str.equals(new String(str.getBytes(encode), encode))) {
				String s3 = encode;
				return s3;
			}
		} catch (Exception ex) {
			logger.warn("exception encountered @ GBK, cause:" + ex.getMessage());
		}
		encode = "ISO-8859-1";
		try {
			if (str.equals(new String(str.getBytes(encode), encode))) {
				String s1 = encode;
				return s1;
			}
		} catch (Exception ex) {
			logger.warn("exception encountered @ ISO-8859-1, cause:" + ex.getMessage());
		}
        encode = "UTF-8";
        try {
            if (str.equals(new String(str.getBytes(encode), encode))) {
                String s1 = encode;
                return s1;
            }
        } catch (Exception ex) {
            logger.warn("exception encountered @ ISO-8859-1, cause:" + ex.getMessage());
        }
		return encode;
	}

	/**
	 * set encoding for document
	 * 
	 * @param doc
	 * @param encoding
	 */
	public static void setEncoding(CrawlDocument doc, String encoding) {
		if (doc == null || encoding == null) {
			return;
		}
		doc.addFeature(COMMON_PROP_ENCODING, encoding);
	}
}
