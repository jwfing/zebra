package org.zebra.spider.plugin;

import java.util.List;
import java.util.ArrayList;
import java.net.*;

import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.zebra.common.Configuration;
import org.zebra.common.Context;
import org.zebra.common.CrawlDocument;
import org.zebra.common.FetchStatus;
import org.zebra.common.UrlInfo;
import org.zebra.common.AtomicCounter;
import org.zebra.common.kestrel.SimpleKestrelQueue;
import org.zebra.common.flow.Processor;
import org.zebra.common.metrics.Metrics;
import org.zebra.common.metrics.MetricsReporter;
import org.zebra.common.utils.ProcessorUtil;

public class KestrelLinkTriger implements Processor, MetricsReporter {
    protected Logger logger = LoggerFactory.getLogger(getClass().getName());
    private SimpleKestrelQueue kestrelQueue;
    private String outgoingQueue = "";
    private AtomicCounter totalCounter = new AtomicCounter();
    private AtomicCounter failedCounter = new AtomicCounter();

    public KestrelLinkTriger() {
    }

    public SimpleKestrelQueue getKestrelQueue() {
        return kestrelQueue;
    }

    public void setKestrelQueue(SimpleKestrelQueue kestrelQueue) {
        this.kestrelQueue = kestrelQueue;
    }

    public String getOutgoingQueue() {
        return outgoingQueue;
    }

    public void setOutgoingQueue(String outgoingQueue) {
        this.outgoingQueue = outgoingQueue;
    }

    @Override
    public boolean destroy() {
        logger.info("destroyed " + getName());
        return true;
    }

    @Override
    public String getName() {
        return getClass().getName();
    }

    @Override
    public boolean initialize() {
        logger.info("initialized " + getName());
        return true;
    }

    public List<Metrics> stat() {
        List<Metrics> stats = new ArrayList<Metrics>();
        stats.add(new Metrics("totalTriger", new Long(totalCounter.get()).toString()));
        stats.add(new Metrics("trigerFailed", new Long(failedCounter.get()).toString()));
        return stats;
    }

    @Override
    public boolean process(CrawlDocument doc, Context context) {
        if (null == doc || null == context) {
            logger.warn("invalid parameter. doc / context is null");
            return false;
        }
        if (null == this.kestrelQueue) {
            logger.warn("kestrelQueue is null.");
            return false;
        }
        if (doc.getFetchStatus() != FetchStatus.OK) {
            return true;
        }
        List<UrlInfo> outlinks = (List<UrlInfo>) context
                .getVariable(ProcessorUtil.COMMON_PROP_OUTLINKS);
        for (UrlInfo link : outlinks) {
            String url = link.getUrl();
            totalCounter.incrementAndGet();
            try {
                this.kestrelQueue.enqueue(this.outgoingQueue, url);
            } catch (Exception ex) {
                logger.warn("encountered exception. url=" + url + " cause: " + ex.getMessage());
                failedCounter.incrementAndGet();
            }
        }
        return true;
    }

}
