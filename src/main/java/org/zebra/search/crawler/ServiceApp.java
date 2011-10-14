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
    public static void main( String[] args )
    {
    	UrlStorage storage = new BDBStorageImpl();
    	((BDBStorageImpl)storage).initialize();
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
