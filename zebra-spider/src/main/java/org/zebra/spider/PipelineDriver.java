package org.zebra.spider;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.zebra.common.*;
import org.zebra.common.flow.*;

public class PipelineDriver {
    private static final Logger logger = Logger.getLogger(PipelineDriver.class.getName());
    private CrawlDocumentCollection collection = null;
    private Dispatcher dispatcher = null;
    private int threadNum = 1;
    private List<Thread> threads = new ArrayList<Thread>();
    Allocator allocator = null;

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
                doc = null;
            }
        }
    }

    public void setDocCollection(CrawlDocumentCollection docCollection) {
        this.collection = docCollection;
    }

    public boolean initialize(String configFile) {
        this.threadNum = Configuration.getIntProperty(Constants.PATH_PIPELINE_THREADS, 1);
        try {
            ApplicationContext appContext = new ClassPathXmlApplicationContext(configFile);
            this.dispatcher = (Dispatcher) appContext.getBean("dispatcher");
            this.allocator = (Allocator) appContext.getBean("allocator");
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
                dispatcher.addChain(ProcessDirectory.USR2_PAGE, chain);
                for (Processor temp : chain.getProcessors()) {
                    logger.info("add processor " + temp.getName() + " to USR2PAGE_CHAIN");
                }
            }
            chain = (ProcessorChain) appContext.getBean("usr3Chain");
            if (chain != null && chain.size() > 0) {
                dispatcher.addChain(ProcessDirectory.USR3_PAGE, chain);
                for (Processor temp : chain.getProcessors()) {
                    logger.info("add processor " + temp.getName() + " to USR3PAGE_CHAIN");
                }
            }
        } catch (Exception ex) {
            logger.fatal("failed to load ./pipeline_config.xml, cause: " + ex.getMessage());
            return false;
        }

        allocator.setCollection(SeedCollection.getInstance());
        boolean result = allocator.initialize();

        logger.info("pipeline driver initialized. threadNum=" + this.threadNum);
        return result;
    }

    public void destroy() {
        if (null != allocator)
            allocator.destory();
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
        Thread thread = null;
        for (int i = 0; i < this.threadNum; ++i) {
            thread = new PipelineThread();
            thread.start();
            threads.add(thread);
        }
    }
}
