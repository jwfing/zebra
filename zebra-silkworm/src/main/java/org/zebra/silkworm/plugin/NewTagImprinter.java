package org.zebra.silkworm.plugin;

import org.apache.log4j.Logger;
import org.zebra.common.*;
import org.zebra.common.flow.*;

public class NewTagImprinter implements Processor {
    private final Logger logger = Logger.getLogger(NewTagImprinter.class);
    private static String dictPath = "";

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
        logger.warn("unsupported-method");
        return false;
    }

}
