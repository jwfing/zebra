package org.zebra.spider.plugin;

import java.util.List;
import java.net.*;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.zebra.common.Configuration;
import org.zebra.common.Context;
import org.zebra.common.CrawlDocument;
import org.zebra.common.UrlInfo;
import org.zebra.common.flow.Processor;
import org.zebra.common.utils.ProcessorUtil;
import org.zebra.spider.Constants;

public class NewLinkTriger implements Processor {
    private final Logger logger = Logger.getLogger(NewLinkTriger.class);
    private HttpClient httpClient = new HttpClient();
    private final static String metaPoint = Configuration.getStringProperty(
            Constants.META_ENDPOINT_URL, "http://localhost:9900/meta?rt=url&u=");
    private final static boolean urlencoding = Configuration.getBooleanProperty(
            Constants.META_ENDPOINT_URLENCODING, false);

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

    @Override
    public boolean process(CrawlDocument doc, Context context) {
        if (null == doc || null == context) {
            logger.warn("invalid parameter. doc / context is null");
            return false;
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
            } finally {
                getMethod.releaseConnection();
            }
        }
        return true;
    }

}
