package org.zebra.common.flow.plugin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zebra.common.*;
import org.zebra.common.flow.*;
import org.zebra.common.utils.ProcessorUtil;

public class CharsetConvertor implements Processor {
    protected Logger logger = LoggerFactory.getLogger(getClass().getName());
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

        if (doc.getFetchStatus() != FetchStatus.OK) {
            return true;
        }

        
        String encoding = doc.getFeature(ProcessorUtil.COMMON_PROP_CONTENTTYPE);
        if (null != encoding) {
            if (encoding.indexOf("charset=") >= 0) { 
                encoding = encoding.substring(encoding.indexOf("charset=") + "charset=".length());
            } else {
                encoding = null;
            }
        }
        if (null == encoding || encoding.isEmpty()) {
            encoding = "gb2312";
        }
        try {
            // convert to UTF-8
            byte[] contentBytes = doc.getContentBytes();
            String old = new String(contentBytes, encoding);
            String result = new String(old.getBytes(), targetCharset);
            doc.setContentString(old);
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
