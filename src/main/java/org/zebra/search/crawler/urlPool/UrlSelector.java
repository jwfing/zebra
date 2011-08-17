package org.zebra.search.crawler.urlPool;

import java.util.List;
import org.zebra.search.crawler.common.*;

public class UrlSelector {
	private UrlStorage storage = null;
	
	public UrlStorage getStorage() {
		return storage;
	}
	public void setStorage(UrlStorage storage) {
		this.storage = storage;
	}
	public List<UrlInfo> retrieveRepeatUrls(int level, int maxCount) {
		if (null != this.storage) {
			return this.storage.selectFromRepeatUrls(level, maxCount);
		}
		return null;
	}
	public List<UrlInfo> retrieveOnceUrls(int maxCount, boolean deleteAfterRetrieve) {
		if (null != this.storage) {
			List<UrlInfo> result = this.storage.selectFromOnceUrls(maxCount);
			if (deleteAfterRetrieve) {
				this.storage.dropUrls(result, Constants.UrlType.ONCE);
			}
			return result;
		}
		return null;
	}
}
