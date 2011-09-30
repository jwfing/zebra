package org.zebra.search.crawler;

import java.util.*;

import org.apache.log4j.Logger;
import org.apache.log4j.Level;

import org.zebra.search.crawler.core.PipelineDriver;
import org.zebra.search.crawler.fetcher.FetcherController;
import org.zebra.search.crawler.urlPool.storage.BDBStorageImpl;
import org.zebra.search.crawler.urlPool.*;
import org.zebra.search.crawler.allocator.*;
import org.zebra.search.crawler.common.*;
import org.zebra.search.crawler.util.*;

/**
 * Hello world!
 *
 */
public class ServiceApp {
	private static final Logger logger = Logger.getLogger(ServiceApp.class);
	private static String[] seedUrls = {
		"http://money.163.com/special/g/00251LR5/gsxw.html",
		"http://money.163.com/special/002534M5/review.html",
		"http://roll.finance.sina.com.cn/finance/zq1/ssgs/index.shtml",
		"http://business.sohu.com/gskb/",
		"http://stock.hexun.com/gsxw/",
		};
    public static void main( String[] args )
    {
    	// add seed
    	UrlStorage storage = new BDBStorageImpl();
    	((BDBStorageImpl)storage).initialize();
    	List<UrlInfo> seeds = new ArrayList<UrlInfo>();
    	for (int i = 0; i < seedUrls.length; i++) {
    		UrlInfo url = new UrlInfo(seedUrls[i]);
    		url.addFeature(ProcessorUtil.COMMON_PROP_FLAG, "seed");
    		seeds.add(url);
    	}
    	storage.addRepeatUrls(seeds, 1);
    	System.out.println("add repeat urls totalSize=" + seeds.size() + " level=1");
    	UrlSelector selector = new UrlSelector();
    	selector.setStorage(storage);
    	SeedCollection collection = SeedCollection.getInstance();
    	UrlAppender appender = UrlAppender.getInstance();
    	appender.setStorage(storage);

    	DefaultAllocator allocator = new DefaultAllocator();
    	allocator.setCollection(collection);
    	allocator.setSelector(selector);
    	allocator.initialize();

    	FetcherController fetcherController = new FetcherController();
    	fetcherController.initialize();

    	PipelineDriver driver = new PipelineDriver();
        driver.initialize();
        driver.start();
    }
}
