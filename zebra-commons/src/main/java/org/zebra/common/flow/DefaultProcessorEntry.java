package org.zebra.common.flow;

import org.apache.log4j.Logger;
import org.zebra.common.Context;
import org.zebra.common.CrawlDocument;
import org.zebra.common.utils.ProcessorUtil;

public class DefaultProcessorEntry implements ProcessorEntry {
    private static final Logger logger = Logger.getLogger(DefaultProcessorEntry.class.getName());

    @Override
    public boolean initialize() {
        return true;
    }

    @Override
    public boolean destroy() {
        return true;
    }

    @Override
    public ProcessDirectory process(CrawlDocument doc, Context context) {
        if (null == doc || null == context) {
            logger.warn("invalid parameter. doc or context is null");
            return null;
        }
        String flag = doc.getFeature(ProcessorUtil.COMMON_PROP_FLAG);
        if (null != flag) {
            if (flag.equalsIgnoreCase(ProcessorUtil.FLAG_VALUE_LIST)) {
                return ProcessDirectory.LIST_PAGE;
            }
            if (flag.equalsIgnoreCase(ProcessorUtil.FLAG_VALUE_CONTENT)) {
                return ProcessDirectory.CONTENT_PAGE;
            }
            if (flag.equalsIgnoreCase(ProcessorUtil.FLAG_VALUE_USR1)) {
                return ProcessDirectory.USR1_PAGE;
            }
            if (flag.equalsIgnoreCase(ProcessorUtil.FLAG_VALUE_USR2)) {
                return ProcessDirectory.USR2_PAGE;
            }
            logger.warn("unknown flag: " + flag);
        } else {
            logger.warn("attribute flag is null");
        }
        return null;
    }

}
