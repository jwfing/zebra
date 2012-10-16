package org.zebra.common.flow;

import java.util.*;

import org.apache.log4j.Logger;
import org.zebra.common.Context;
import org.zebra.common.CrawlDocument;

public class ProcessorChain {
    private static final Logger logger = Logger.getLogger(ProcessorChain.class.getName());
    private List<Processor> processors = new ArrayList<Processor>();

    public void setProcessors(List<Processor> list) {
        this.processors = list;
    }

    public List<Processor> getProcessors() {
        return this.processors;
    }

    public void addProcessor(Processor processor) {
        this.processors.add(processor);
    }

    public boolean process(CrawlDocument doc, Context context) {
        for (Processor processor : processors) {
            try {
                if (!processor.process(doc, context)) {
                    logger.warn("failed to process doc(" + doc.getUrl() + ") by processor("
                            + processor.getName() + ")");
                    return false;
                }
            } catch (Exception ex) {
                logger.warn("failed to process doc(" + doc.getUrl() + ") by processor("
                        + processor.getName() + ")" + ", exception=" + ex.getMessage());
                return false;
            }
        }
        return true;
    }

    public int size() {
        return this.processors.size();
    }
}
