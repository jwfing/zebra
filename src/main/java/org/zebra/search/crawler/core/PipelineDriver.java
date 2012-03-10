package org.zebra.search.crawler.core;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.zebra.search.crawler.fetcher.*;
import org.zebra.search.crawler.common.*;
import org.zebra.search.crawler.plugin.*;

public class PipelineDriver {
    private static final Logger logger = Logger.getLogger(PipelineDriver.class.getName());
    private CrawlDocumentCollection collection = CrawlDocumentCollection.getInstance();
    private Dispatcher dispatcher = null;
    private int threadNum = 1;
    private List<Thread> threads = new ArrayList<Thread>();

    private class PipelineThread extends Thread {
        public void run() {
            if (null == collection || null == dispatcher) {
                logger.error("collection or dispatcher is null!");
                return;
            }
            while (isAlive()) {
                CrawlDocument doc = collection.poll();
                if (null == doc) {
                    logger.debug("have no document in pipeline, sleep 5000ms");
                    try {
                        sleep(5000);
                    } catch (Exception ex) {
                        logger.warn("failed to sleep 50ms. cause:" + ex.getMessage());
                    }
                    continue;
                } else {
                    boolean ret = dispatcher.process(doc);
                    if (!ret) {
                        logger.warn("failed to process doc. url=" + doc.getUrl());
                    }
                }
            }
        }
    }

    public void initialize() {
        this.threadNum = Configuration.getIntProperty(Configuration.PATH_PIPELINE_THREADS, 1);
        try {
            ApplicationContext appContext = new ClassPathXmlApplicationContext(
                    "./pipeline_config.xml");
            this.dispatcher = (Dispatcher) appContext.getBean("dispatcher");
            ProcessorChain chain = (ProcessorChain) appContext.getBean("listChain");
            if (chain != null && chain.size() > 0) {
                dispatcher.addChain(ProcessDirectory.LIST_PAGE, chain);
                for (Processor temp : chain.getProcessors()) {
                    logger.info("add processor " + temp.getName() + " to LISTPAGE_CHAIN");
                }
            }
            chain = (ProcessorChain) appContext.getBean("contentChain");
            if (chain != null && chain.size() > 0) {
                dispatcher.addChain(ProcessDirectory.CONTENT_PAGE, chain);
                for (Processor temp : chain.getProcessors()) {
                    logger.info("add processor " + temp.getName() + " to CONTENTPAGE_CHAIN");
                }
            }
            chain = (ProcessorChain) appContext.getBean("usr1Chain");
            if (chain != null && chain.size() > 0) {
                dispatcher.addChain(ProcessDirectory.USR1_PAGE, chain);
                for (Processor temp : chain.getProcessors()) {
                    logger.info("add processor " + temp.getName() + " to USR1PAGE_CHAIN");
                }
            }
            chain = (ProcessorChain) appContext.getBean("usr2Chain");
            if (chain != null && chain.size() > 0) {
                dispatcher.addChain(ProcessDirectory.USE2_PAGE, chain);
                for (Processor temp : chain.getProcessors()) {
                    logger.info("add processor " + temp.getName() + " to USR2PAGE_CHAIN");
                }
            }
            chain = (ProcessorChain) appContext.getBean("usr3Chain");
            if (chain != null && chain.size() > 0) {
                dispatcher.addChain(ProcessDirectory.USE3_PAGE, chain);
                for (Processor temp : chain.getProcessors()) {
                    logger.info("add processor " + temp.getName() + " to USR3PAGE_CHAIN");
                }
            }
        } catch (Exception ex) {
            logger.fatal("failed to load ./pipeline_config.xml, cause: " + ex.getMessage());
        }

        logger.info("pipeline driver initialized. threadNum=" + this.threadNum);
    }

    public void destroy() {
        for (Thread thread : threads) {
            try {
                thread.join(50);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void start() {
        logger.info("begin to start pipeline driver. threadNum=" + this.threadNum);
        // ExecutorService pool = Executors.newFixedThreadPool(this.threadNum);
        Thread thread = null;
        for (int i = 0; i < this.threadNum; ++i) {
            thread = new PipelineThread();
            thread.start();
            threads.add(thread);
        }
        // pool.shutdown();
    }
}
