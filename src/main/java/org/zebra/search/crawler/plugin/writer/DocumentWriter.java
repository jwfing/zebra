package org.zebra.search.crawler.plugin.writer;

import org.zebra.search.crawler.common.*;

public class DocumentWriter {
    private WritableContent content = null;
    private Persistence persistence = null;
    public boolean write(CrawlDocument doc) {
    	if (null == content || null == persistence) {
    		return false;
    	}
    	content.reset(doc);
    	byte[] bytes = content.getBytes();
    	return persistence.writeBytes(bytes);
    }
}
