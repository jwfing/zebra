package org.zebra.spider.tools;

import java.io.*;
import java.util.List;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zebra.common.domain.dao.SeedDao;
import org.zebra.common.domain.Seed;
import org.zebra.common.UrlInfo;

public class ListSeed {
    protected Logger logger = LoggerFactory.getLogger(getClass().getName());
    private SeedDao dao;
    public void setSeedDao(SeedDao dao) {
        this.dao = dao;
    }

    public ListSeed(SeedDao dao) {
        this.dao = dao;
    }

    public int execute(String localFile) {
		List<UrlInfo> result = new ArrayList<UrlInfo>();
		int offset = 0;
		List<Seed> seeds = this.dao.getSeeds(1999999999, offset, 1000);
		while(null != seeds && seeds.size() > 0) {
		    offset += 1000;
		    for (Seed seed : seeds) {
		        result.add(new UrlInfo(seed.getUrl()));
		    }
		    seeds = this.dao.getSeeds(0, offset, 1000);
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
