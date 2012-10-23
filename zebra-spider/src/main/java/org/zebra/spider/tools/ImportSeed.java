package org.zebra.spider.tools;

import java.io.*;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zebra.common.domain.dao.SeedDao;
import org.zebra.common.domain.Seed;
import org.zebra.common.utils.StringUtil;
import org.zebra.common.UrlInfo;

public class ImportSeed {
    protected Logger logger = LoggerFactory.getLogger(getClass().getName());
    private SeedDao dao;
    public void setSeedDao(SeedDao dao) {
        this.dao = dao;
    }

	public ImportSeed(SeedDao dao) {
	    this.dao = dao;
	}

	private List<UrlInfo> readFile(String localFile) {
		List<UrlInfo> urlInfos= new ArrayList<UrlInfo>();
		File file = new File(localFile);
		if (!file.exists()) {
			logger.warn("local file doesn't exist. path=" + localFile);
			return urlInfos;
		}
		try {
		    FileReader freader = new FileReader(file);
		    BufferedReader reader = new BufferedReader(freader);
		    String line = null;

		    while ((line = reader.readLine()) != null) {
		    	line = line.trim();
		    	if (line.isEmpty() || line.startsWith("#")) {
		    		continue;
		    	}
		    	String[] sections = line.split("\t");
		    	UrlInfo url = new UrlInfo(sections[0]);
		    	for (int i = 1; i < sections.length; ++i) {
		    		String temp = sections[i];
		    		String[] kv = temp.split("=");
		    		if (kv.length != 2) {
		    			continue;
		    		}
		    		url.addFeature(kv[0], kv[1]);
		    	}
		    	urlInfos.add(url);
		    }
		    reader.close();
		    freader.close();
		} catch (Exception ex) {
		    ex.printStackTrace();
			logger.warn("failed to read localfile. path=" + localFile + ", cause=" + ex.getMessage());
		}
		System.out.println("read " + urlInfos.size() + " urls from file:" + localFile);
		return urlInfos;
	}

	public int execute(String localFile) {
		List<UrlInfo> urls = readFile(localFile);
		long now = System.currentTimeMillis() / 1000;
		long fetchPeriod = 43200; // half of a day
		for (UrlInfo url : urls) {
		    Seed seed = new Seed();
		    seed.setUrl(url.getUrl());
		    seed.setTags((String)url.getFeature("tags"));
		    seed.setStrict((String)url.getFeature("strict"));
		    seed.setNextFetch(now);
		    seed.setUpdatePeriod(fetchPeriod);
		    seed.setTimeCreated(now);
		    seed.setUrlMd5(StringUtil.computeMD5(url.getUrl()));
		    try {
    		    this.dao.save(seed);
                logger.info("insert seed: " + url.getUrl());
		    } catch (Exception ex) {
		        logger.warn("failed to insert seed: " + url.getUrl() + ", cause: " + ex.getMessage());
		    }
		}
		return 0;
	}
}
