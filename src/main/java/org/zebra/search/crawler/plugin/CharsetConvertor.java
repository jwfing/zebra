package org.zebra.search.crawler.plugin;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.zebra.search.crawler.common.*;
import org.zebra.search.crawler.util.ProcessorUtil;

public class CharsetConvertor implements Processor {
    private final Logger logger = Logger.getLogger(UrlPoolWriter.class);
    private static final String DEFAULT_TARGET_CHARSET = "utf-8";
    private String targetCharset = DEFAULT_TARGET_CHARSET;

    public String getTargetCharset() {
        return targetCharset;
    }

    public void setTargetCharset(String targetCharset) {
        this.targetCharset = targetCharset;
    }

    public boolean initialize() {
        return true;
    }

    public boolean destroy() {
        return true;
    }

    public String getName() {
        return this.getClass().getName();
    }

    public boolean process(CrawlDocument doc, Context context) {
        if (null == doc || null == context) {
            logger.warn("parameter is invalid");
            return false;
        }

        String encoding = doc.getFeature(ProcessorUtil.COMMON_PROP_CONTENTTYPE);
        if (null != encoding) {
            if (encoding.indexOf("charset=") >= 0) { 
               encoding = encoding.substring(encoding.indexOf("charset=") + "charset=".length());
            } else {
                encoding = null;
            }
        }
        try {
            // convert to UTF-8
            byte[] contentBytes = doc.getContentBytes();
            String old = null;
            if (encoding != null) {
                old = new String(contentBytes, encoding);
            } else {
                old = new String(contentBytes);
            }
            String result = new String(old.getBytes(), this.targetCharset);
            doc.setContentString(result);
            doc.addFeature(ProcessorUtil.COMMON_PROP_ENCODING, encoding);
            context.setVariable(ProcessorUtil.COMMON_PROP_OLDCONTENT, contentBytes);
            logger.info("convert doc(" + doc.getUrl() + ") content from " + encoding + " to "
                    + this.targetCharset);
        } catch (Exception ex) {
            logger.warn("error ocurred for charset convertor for url=" + doc.getUrl() + ". cause:" + ex.getMessage());
            return false;
        }
        return true;
    }

}
