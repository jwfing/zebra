package org.zebra.common.flow;

import org.zebra.common.*;

public interface Processor {
	public boolean initialize();
	public boolean destroy();
	public String getName();
    public boolean process(CrawlDocument doc, Context context);
}
