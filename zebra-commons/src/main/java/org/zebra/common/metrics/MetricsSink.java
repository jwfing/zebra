package org.zebra.common.metrics;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MetricsSink {
    protected Logger logger = LoggerFactory.getLogger(getClass().getName());
	private static MetricsSink instance = null;
	private List<MetricsReporter> reporters = new ArrayList<MetricsReporter>();
	private int sleepInterval = 3;
	private class Gather extends Thread {
		public void run() {
			while(isAlive()) {
				for (MetricsReporter reporter : reporters) {
					List<Metrics> status = reporter.stat();
					StringBuilder sb = new StringBuilder();
					for (Metrics metrics : status) {
					    sb.append(metrics.toString() + " ");
					}
                    logger.info("[METRICS_STAT] " + sb.toString());
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
