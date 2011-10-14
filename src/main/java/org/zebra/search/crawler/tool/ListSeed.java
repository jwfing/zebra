package org.zebra.search.crawler.tool;

import java.io.*;
import java.util.List;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.zebra.search.crawler.common.UrlInfo;
import org.zebra.search.crawler.urlPool.*;

public class ListSeed {
	private static final Logger logger = Logger.getLogger(ImportSeed.class);
	private UrlSelector selector = new UrlSelector();

	public ListSeed(UrlStorage obj) {
		this.selector.setStorage(obj);
	}

	public int execute(String localFile, int level) {
		int maxCount = 1024;
		List<UrlInfo> result = new ArrayList<UrlInfo>();
		if (-1 == level) {
			// export all
			for (int i = 0; i < 10; i++) {
				List<UrlInfo> tmp = this.selector.retrieveRepeatUrls(i, 0, maxCount);
				if (null != tmp && tmp.size() > 0) {
					result.addAll(tmp);
				}
			}
		} else {
			List<UrlInfo> tmp = this.selector.retrieveRepeatUrls(level, 0, maxCount);
			if (null != tmp && tmp.size() > 0) {
				result.addAll(tmp);
			}
		}
		if (null == localFile || localFile.isEmpty()) {
			for (UrlInfo url : result) {
				System.out.println(url.toString());
			}
		} else {
			File file = new File(localFile);
			try {
			    FileOutputStream fos = new FileOutputStream(file);
			    for (UrlInfo url : result) {
			    	fos.write(url.toString().getBytes());
			    	fos.write('\n');
			    }
			    fos.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return 0;
	}
}
