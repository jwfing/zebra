package org.zebra.common.flow;

import java.util.*;

import org.perf4j.StopWatch;
import org.perf4j.slf4j.Slf4JStopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zebra.common.Context;
import org.zebra.common.CrawlDocument;

public class ProcessorChain {
    protected Logger logger = LoggerFactory.getLogger(getClass().getName());
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
                Slf4JStopWatch processWatcher = new Slf4JStopWatch(processor.getName());
                if (!processor.process(doc, context)) {
                    processWatcher.stop();
                    logger.warn("failed to process doc(" + doc.getUrl() + ") by processor("
                            + processor.getName() + ")");
                    return false;
                }
                processWatcher.stop();
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
