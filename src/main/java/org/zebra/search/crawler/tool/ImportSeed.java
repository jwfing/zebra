package org.zebra.search.crawler.tool;

import java.io.*;
import java.util.*;

import org.apache.log4j.Logger;
import org.zebra.search.crawler.urlPool.*;
import org.zebra.search.crawler.common.UrlInfo;

public class ImportSeed {
	private static final Logger logger = Logger.getLogger(ImportSeed.class);
	private UrlAppender appender = UrlAppender.getInstance();

	public ImportSeed(UrlStorage obj) {
		this.appender.setStorage(obj);
	}

	private Map<Integer, List<UrlInfo> > readFile(String localFile, int filterLevel) {
		Map<Integer, List<UrlInfo> > urlMaps = new HashMap<Integer, List<UrlInfo> >();
		File file = new File(localFile);
		if (!file.exists()) {
			logger.warn("local file doesn't exist. path=" + localFile);
			return urlMaps;
		}
		try {
		    FileReader freader = new FileReader(file);
		    BufferedReader reader = new BufferedReader(freader);
		    String line = null;
		    int levelValue = 0;
		    
		    while ((line = reader.readLine()) != null) {
		    	line = line.trim();
		    	if (line.isEmpty() || line.startsWith("#")) {
		    		continue;
		    	}
		    	String[] sections = line.split("\t");
		    	UrlInfo url = new UrlInfo(sections[0]);
		    	levelValue = -1;
		    	for (int i = 1; i < sections.length; ++i) {
		    		String temp = sections[i];
		    		String[] kv = temp.split("=");
		    		if (kv.length != 2) {
		    			continue;
		    		}
		    		if (kv[0].equalsIgnoreCase("level")) {
		    			levelValue = new Integer(kv[1]).intValue();
		    			if (!urlMaps.containsKey(levelValue)) {
		    				urlMaps.put(levelValue, new ArrayList<UrlInfo>());
		    			}
		    		} else {
		    			url.addFeature(kv[0], kv[1]);
		    		}
		    	}
		    	if (-1 == levelValue) {
		    		levelValue = 5;
		    	}
		    	if (filterLevel != -1) {
		    		if (filterLevel != levelValue) {
		    			logger.info("skip url because of level filter. line=" + line);
		    			continue;
		    		}
		    	}
		    	urlMaps.get(levelValue).add(url);
		    }
		    reader.close();
		    freader.close();
		} catch (Exception ex) {
		    ex.printStackTrace();
			logger.warn("failed to read localfile. path=" + localFile + ", cause=" + ex.getMessage());
		}
		return urlMaps;
	}

	public int execute(String localFile, int level) {
		Map<Integer, List<UrlInfo> > urls = readFile(localFile, level);
		Set<Map.Entry<Integer, List<UrlInfo> > > entries = urls.entrySet();
		for (Map.Entry<Integer, List<UrlInfo> > entry : entries) {
			Integer urlLevel = entry.getKey();
			List<UrlInfo> urlList = entry.getValue();
			if (null == urlLevel || null == urlList || urlList.size() < 1) {
				continue;
			}
			this.appender.appendRepeatUrls(urlList, urlLevel.intValue());
		}
		return 0;
	}
}
