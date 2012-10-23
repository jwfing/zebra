package org.zebra.spider;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.zebra.common.*;
import org.zebra.common.flow.*;
import org.zebra.common.metrics.MetricsReporter;
import org.zebra.common.metrics.MetricsSink;

public class PipelineDriver {
    protected Logger logger = LoggerFactory.getLogger(getClass().getName());
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
                    logger.debug("have no document in pipeline, sleep 90s");
                    try {
                        sleep(90000);
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
            MetricsSink sink = MetricsSink.getInstance();
            ApplicationContext appContext = new ClassPathXmlApplicationContext(configFile);
            this.dispatcher = (Dispatcher) appContext.getBean("dispatcher");
            this.allocator = (Allocator) appContext.getBean("allocator");
            ProcessorChain chain = (ProcessorChain) appContext.getBean("listChain");
            if (chain != null && chain.size() > 0) {
                dispatcher.addChain(ProcessDirectory.LIST_PAGE, chain);
                for (Processor temp : chain.getProcessors()) {
                    logger.info("add processor " + temp.getName() + " to LISTPAGE_CHAIN");
                    if (temp instanceof MetricsReporter) {
                        sink.register((MetricsReporter)temp);
                    }
                }
            }
            chain = (ProcessorChain) appContext.getBean("contentChain");
            if (chain != null && chain.size() > 0) {
                dispatcher.addChain(ProcessDirectory.CONTENT_PAGE, chain);
                for (Processor temp : chain.getProcessors()) {
                    logger.info("add processor " + temp.getName() + " to CONTENTPAGE_CHAIN");
                    if (temp instanceof MetricsReporter) {
                        sink.register((MetricsReporter)temp);
                    }
                }
            }
            chain = (ProcessorChain) appContext.getBean("usr1Chain");
            if (chain != null && chain.size() > 0) {
                dispatcher.addChain(ProcessDirectory.USR1_PAGE, chain);
                for (Processor temp : chain.getProcessors()) {
                    logger.info("add processor " + temp.getName() + " to USR1PAGE_CHAIN");
                    if (temp instanceof MetricsReporter) {
                        sink.register((MetricsReporter)temp);
                    }
                }
            }
            chain = (ProcessorChain) appContext.getBean("usr2Chain");
            if (chain != null && chain.size() > 0) {
                dispatcher.addChain(ProcessDirectory.USR2_PAGE, chain);
                for (Processor temp : chain.getProcessors()) {
                    logger.info("add processor " + temp.getName() + " to USR2PAGE_CHAIN");
                    if (temp instanceof MetricsReporter) {
                        sink.register((MetricsReporter)temp);
                    }
                }
            }
            chain = (ProcessorChain) appContext.getBean("usr3Chain");
            if (chain != null && chain.size() > 0) {
                dispatcher.addChain(ProcessDirectory.USR3_PAGE, chain);
                for (Processor temp : chain.getProcessors()) {
                    logger.info("add processor " + temp.getName() + " to USR3PAGE_CHAIN");
                    if (temp instanceof MetricsReporter) {
                        sink.register((MetricsReporter)temp);
                    }
                }
            }
        } catch (Exception ex) {
            logger.warn("failed to load ./pipeline_config.xml, cause: " + ex.getMessage());
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
        Thread thread = null;
        for (int i = 0; i < this.threadNum; ++i) {
            thread = new PipelineThread();
            thread.start();
            threads.add(thread);
            logger.info("begin to start pipeline driver. threadIdx=" + i);
        }
    }
}
