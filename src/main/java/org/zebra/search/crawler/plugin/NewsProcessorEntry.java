package org.zebra.search.crawler.plugin;

import org.zebra.search.crawler.common.Context;
import org.zebra.search.crawler.common.CrawlDocument;
import org.zebra.search.crawler.core.ProcessDirectory;
import org.zebra.search.crawler.core.ProcessorEntry;
import org.zebra.search.crawler.util.ProcessorUtil;

public class NewsProcessorEntry implements ProcessorEntry{
	public final static String SEED_FLAG = "seed";
	public final static String PAGE_FLAG = "page";
	public final static String BINARY_FLAG = "binary";
    public boolean initialize() {
    	return true;
    }
    public boolean destroy() {
    	return false;
    }
    public ProcessDirectory process(CrawlDocument doc, Context context) {
    	if (null == doc || null == context) {
    		return null;
    	}
    	String flag = doc.getFeature(ProcessorUtil.COMMON_PROP_FLAG);
    	if (null != flag) {
        	if (flag.equalsIgnoreCase(SEED_FLAG)) {
        		return ProcessDirectory.LIST_PAGE;
        	}
        	if (flag.equalsIgnoreCase(PAGE_FLAG)) {
        		return ProcessDirectory.CONTENT_PAGE;
        	}
        	if (flag.equalsIgnoreCase(BINARY_FLAG)) {
        		return ProcessDirectory.USR1_PAGE;
        	}
    	}
    	return null;
    }

}
