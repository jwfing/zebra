package org.zebra.spider.plugin;

import java.util.List;
import java.util.ArrayList;
import java.net.*;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zebra.common.Configuration;
import org.zebra.common.Context;
import org.zebra.common.CrawlDocument;
import org.zebra.common.FetchStatus;
import org.zebra.common.UrlInfo;
import org.zebra.common.AtomicCounter;
import org.zebra.common.flow.Processor;
import org.zebra.common.metrics.Metrics;
import org.zebra.common.metrics.MetricsReporter;
import org.zebra.common.utils.ProcessorUtil;
import org.zebra.spider.Constants;

public class NewLinkTriger implements Processor, MetricsReporter {
    protected Logger logger = LoggerFactory.getLogger(getClass().getName());
    private HttpClient httpClient = new HttpClient();
    private final static String metaPoint = Configuration.getStringProperty(
            Constants.META_ENDPOINT_URL, "http://localhost:9900/meta?rt=url&u=");
    private final static boolean urlencoding = Configuration.getBooleanProperty(
            Constants.META_ENDPOINT_URLENCODING, false);
    private AtomicCounter totalCounter = new AtomicCounter();
    private AtomicCounter failedCounter = new AtomicCounter();

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
        if (doc.getFetchStatus() != FetchStatus.OK) {
            return true;
        }
        List<UrlInfo> outlinks = (List<UrlInfo>) context
                .getVariable(ProcessorUtil.COMMON_PROP_OUTLINKS);
        for (UrlInfo link : outlinks) {
            String url = link.getUrl();
            if (urlencoding) {
                try {
                    url = URLEncoder.encode(link.getUrl(), "utf-8");
                } catch (Exception ex) {
                    url = link.getUrl();
                    logger.warn("encountered exception while urlencoding. url=" + url);
                }
            }
            totalCounter.incrementAndGet();
            GetMethod getMethod = new GetMethod(metaPoint + url);
            getMethod.getParams().setParameter(
                    HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
            try {
                int statusCode = httpClient.executeMethod(getMethod);
                if (HttpStatus.SC_OK != statusCode) {
                    logger.warn("status code(" + statusCode + ") is invalid. url=" + url);
                }
            } catch (Exception ex) {
                logger.warn("encountered exception. url=" + url + " cause: " + ex.getMessage());
                failedCounter.incrementAndGet();
            } finally {
                getMethod.releaseConnection();
            }
        }
        return true;
    }

}
