package org.zebra.search.crawler.allocator;

import java.util.List;
import org.zebra.search.crawler.common.UrlInfo;

public interface Allocator {
	public void initialize();
	public void destory();
    public List<UrlInfo> getUrls(int max);
}
