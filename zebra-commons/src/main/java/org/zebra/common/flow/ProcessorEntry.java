package org.zebra.common.flow;

import org.zebra.common.Context;
import org.zebra.common.CrawlDocument;

public interface ProcessorEntry {
    public boolean initialize();

    public boolean destroy();

    public ProcessDirectory process(CrawlDocument doc, Context context);
}
