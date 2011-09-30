package org.zebra.search.crawler.metrics;

import java.util.*;

import org.apache.log4j.Logger;

public class MetricsSink {
	private static final Logger logger = Logger.getLogger(MetricsSink.class);
	private static MetricsSink instance = null;
	private List<MetricsReporter> reporters = new ArrayList<MetricsReporter>();
	private int sleepInterval = 5;
	private class Gather extends Thread {
		public void run() {
			while(isAlive()) {
				for (MetricsReporter reporter : reporters) {
					List<Metrics> status = reporter.stat();
					for (Metrics metrics : status) {
						System.out.print(metrics.getKey() + "=" + metrics.getValue() + " ");
					}
					System.out.println("");
				}
				try {
				    sleep(sleepInterval * 60000);
				} catch (Exception ex) {
					logger.warn("exception encountered. cause:" + ex.getMessage());
				}
			}
		}
	}
	private Gather gather = new Gather();

	public static MetricsSink getInstance() {
    	if (null == instance) {
    		synchronized(MetricsSink.class) {
    			if (null == instance) {
    				logger.info("create MetricsSink Instance.");
    				instance = new MetricsSink();
    			}
    		}
    	}
    	return instance;
    }

	private MetricsSink() {
		;
	}

	public void register(MetricsReporter reporter) {
		if (null != reporter) {
			this.reporters.add(reporter);
		}
	}

	public void start() {
		this.gather.start();
	}

	public void stop() {
		try {
			this.gather.join(3000);
		} catch (Exception ex) {
			logger.warn("exception encountered, cause:" + ex.getMessage());
		}
	}
}
