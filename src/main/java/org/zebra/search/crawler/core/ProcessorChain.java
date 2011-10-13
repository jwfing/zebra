package org.zebra.search.crawler.core;

import java.util.*;

import org.apache.log4j.Logger;
import org.zebra.search.crawler.common.Context;
import org.zebra.search.crawler.common.CrawlDocument;
import org.zebra.search.crawler.common.Processor;

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
    		if (!processor.process(doc, context)) {
    			logger.warn("failed to process doc(" + doc.getUrl() + ") by processor(" + processor.getName() + ")");
    			return false;
    		}
    	}
    	return true;
    }
    public int size() {
    	return this.processors.size();
    }
}
