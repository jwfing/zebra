package org.zebra.spider;

import java.util.*;

import org.zebra.common.CrawlDocumentCollection;
import org.zebra.common.http.HttpClientFetcher;
import org.zebra.common.metrics.*;

/**
 * Hello world!
 * 
 */
public class ServiceApp {
    public static void main(String[] args) {
        String properties = System.getProperty("core.properties");
        if (null == properties || properties.isEmpty()) {
            properties = "spider_context.xml";
        }

        SeedCollection seedCollection = SeedCollection.getInstance();
        CrawlDocumentCollection docCollection = CrawlDocumentCollection.getInstance();

        PipelineDriver driver = new PipelineDriver();
        driver.setDocCollection(docCollection);
        if (!driver.initialize(properties)) {
            System.exit(-1);
        }
        driver.start();

        FetcherController fetcherController = new FetcherController();
        fetcherController.setDocCollection(docCollection);
        fetcherController.setSeedCollection(seedCollection);
        fetcherController.initialize();

        LoadMonitor loadMonitor = new LoadMonitor();
        MetricsSink sink = MetricsSink.getInstance();
        sink.register(seedCollection);
        sink.register(docCollection);
        sink.register(loadMonitor);
        sink.start();
    }
}
