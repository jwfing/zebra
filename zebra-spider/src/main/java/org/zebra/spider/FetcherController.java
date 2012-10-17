package org.zebra.spider;

import java.util.List;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import org.zebra.common.*;
import org.zebra.common.domain.*;
import org.zebra.common.http.*;
import org.zebra.common.utils.ProcessorUtil;

public class FetcherController {
    private static final Logger logger = Logger.getLogger(FetcherController.class.getName());
    private SeedCollection seedCollection = null;
    private CrawlDocumentCollection docCollection = null;

    public SeedCollection getSeedCollection() {
        return seedCollection;
    }

    public void setSeedCollection(SeedCollection seedCollection) {
        this.seedCollection = seedCollection;
    }

    public CrawlDocumentCollection getDocCollection() {
        return docCollection;
    }

    public void setDocCollection(CrawlDocumentCollection docCollection) {
        this.docCollection = docCollection;
    }

    private static class FetcherThread extends Thread {
        private Fetcher fetcher = null;

        private SeedCollection seedCollection = null;
        private CrawlDocumentCollection docCollection = null;

        public FetcherThread(Fetcher fetcher, SeedCollection seedCollection,
                CrawlDocumentCollection docCollection) {
            this.fetcher = fetcher;
            this.seedCollection = seedCollection;
            this.docCollection = docCollection;
        }

        public void run() {
            List<Seed> urls = null;
            if (null == this.fetcher || null == this.seedCollection || null == this.docCollection) {
                logger.warn("fetcher / seed collection / doc collection is null");
                return;
            }
            while (isAlive()) {
                urls = this.seedCollection.getSeeds(10);
                if (null == urls || urls.size() <= 0) {
                    try {
                        logger.debug("have no url to fetch, sleep 10s");
                        sleep(10000);
                    } catch (Exception ex) {
                        logger.warn("failed to sleep. cause:" + ex.getMessage());
                    }
                    continue;
                }
                for (Seed url : urls) {
                    UrlInfo urlInfo = new UrlInfo(url.getUrl());
                    urlInfo.addFeature(ProcessorUtil.COMMON_PROP_FLAG, ProcessorUtil.FLAG_VALUE_LIST);
                    CrawlDocument doc = this.fetcher.fetchDocument(urlInfo);
                    if (null == doc) {
                        logger.warn("failed to fetch document. url=" + url.getUrl());
                    } else if (doc.getFetchStatus() != FetchStatus.OK) {
                        logger.warn("failed to fetch document. url=" + url.getUrl() + ", fetchStatus=" + doc.getFetchStatus());
                        doc = null;
                    }else {
                        logger.debug("success to fetch document. url=" + url.getUrl());
                        this.docCollection.offer(doc);
                    }
                }
            }
        }
    }

    private List<FetcherThread> threads = new ArrayList<FetcherThread>();

    public void initialize() {
        HttpClientFetcher.startConnectionMonitorThread();

        int threadNum = Configuration.getIntProperty(Constants.PATH_FETCHER_NUM, 1);
        if (threadNum <= 0) {
            logger.warn("fetcher thread num is invalid!");
            threadNum = 1;
        }
        SeedCollection seedCollection = SeedCollection.getInstance();
        CrawlDocumentCollection docCollection = CrawlDocumentCollection.getInstance();
        for (int i = 0; i < threadNum; i++) {
            FetcherThread thread = new FetcherThread(new HttpClientFetcher(), seedCollection,
                    docCollection);
            thread.start();
            threads.add(thread);
            logger.info("create fetcher thread :" + i);
        }
    }

    public void destroy() {
        for (FetcherThread thread : threads) {
            try {
                thread.join(1000);
            } catch (Exception ex) {
                logger.warn("failed to stop fetcher thread, cause:" + ex.getMessage());
            }
        }
        HttpClientFetcher.stopConnectionMonitorThread();
    }
}