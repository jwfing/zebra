package org.zebra.spider;

import java.util.*;

import org.apache.log4j.Logger;
import org.apache.log4j.Level;
import org.zebra.common.CrawlDocumentCollection;

/**
 * Hello world!
 * 
 */
public class ServiceApp {
    private static final Logger logger = Logger.getLogger(ServiceApp.class);

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

        while (true) {
            try {
                Thread.sleep(30000);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }
    }
}
