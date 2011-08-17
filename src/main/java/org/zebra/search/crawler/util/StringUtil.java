package org.zebra.search.crawler.util;

public class StringUtil {
	public static long FNVHash(String data) {
		final int p = 16777619;
		long hash = 2166136261L;
		for (int i = 0; i < data.length(); i++)
			hash = (hash ^ data.charAt(i)) * p;
		hash += hash << 13;
		hash ^= hash >> 7;
		hash += hash << 3;
		hash ^= hash >> 17;
		hash += hash << 5;
		return hash;
	}

}
