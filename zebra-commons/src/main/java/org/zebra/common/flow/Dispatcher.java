package org.zebra.common.flow;

import java.util.*;

import org.apache.log4j.Logger;
import org.zebra.common.*;

public class Dispatcher {
    private static final Logger logger = Logger.getLogger(Dispatcher.class.getName());
    private ProcessorEntry entry = null;
    private Map<ProcessDirectory, ProcessorChain> chains = new HashMap<ProcessDirectory, ProcessorChain>();

    public ProcessorEntry getEntry() {
        return entry;
    }

    public void setEntry(ProcessorEntry entry) {
        this.entry = entry;
    }

    public boolean addChain(ProcessDirectory directory, ProcessorChain chain) {
        if (null == directory || null == chain || chain.size() <= 0) {
            logger.warn("parameter is invalid.");
            return false;
        }
        if (this.chains.containsKey(directory)) {
            logger.warn("ProcessDirectory has been added.");
            return false;
        }
        this.chains.put(directory, chain);
        return true;
    }

    public Map<ProcessDirectory, ProcessorChain> getChains() {
        return chains;
    }

    public void setChains(Map<ProcessDirectory, ProcessorChain> chains) {
        this.chains = chains;
    }

    public boolean process(CrawlDocument doc) {
        if (null == this.entry || this.chains.size() <= 0) {
            logger.warn("internal status is error(entry is null or chains.size() equals zero)");
            return false;
        }
        Context context = new Context();
        ProcessDirectory directory = this.entry.process(doc, context);
        if (!this.chains.containsKey(directory)) {
            logger.warn("can't found the process chain for directory(" + directory + ")");
            return false;
        }
        ProcessorChain chain = this.chains.get(directory);
        return chain.process(doc, context);
    }
}
