package org.zebra.search.crawler.plugin;

import org.apache.log4j.Logger;
import org.zebra.search.crawler.common.*;
import org.zebra.search.crawler.util.ProcessorUtil;

public class CharsetConvertor implements Processor {
    private final Logger logger = Logger.getLogger(UrlPoolWriter.class);
    private static final String DEFAULT_TARGET_CHARSET = "UTF-8";
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

        String content = doc.getContentString();
        String encoding = ProcessorUtil.getEncoding(content);
        logger.debug("doc(" + doc.getUrl() + ") encoding is:" + encoding);
        try {
            String result = new String(content.getBytes(/*encoding*/), this.targetCharset);
            doc.setContentString(result);
            doc.addFeature(ProcessorUtil.COMMON_PROP_ENCODING, encoding);
            logger.info("convert doc(" + doc.getUrl() + ") content from " + encoding + " to "
                    + this.targetCharset);
        } catch (Exception ex) {
            logger.warn("error ocurred for charset convertor. cause:" + ex.getMessage());
            return false;
        }
        return true;
    }

}
