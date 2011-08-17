package org.zebra.search.crawler.urlPool;

import java.util.*;
import org.zebra.search.crawler.common.UrlInfo;

public interface UrlStorage {
	public boolean addRepeatUrls(List<UrlInfo> urls, int level);
	public boolean addOnceUrls(List<UrlInfo> urls);
	public List<UrlInfo> selectFromRepeatUrls(int level, int maxCount);
	public List<UrlInfo> selectFromOnceUrls(int maxCount);
	public boolean dropUrls(List<UrlInfo> urls, Constants.UrlType type);
}
