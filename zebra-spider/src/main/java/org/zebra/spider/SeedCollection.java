package org.zebra.spider;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.zebra.common.domain.Seed;
import org.zebra.common.metrics.Metrics;
import org.zebra.common.metrics.MetricsReporter;

@Component
public class SeedCollection implements MetricsReporter {
    protected Logger logger = LoggerFactory.getLogger(getClass().getName());
    private static SeedCollection instance = null;
    private ConcurrentLinkedQueue<Seed> queue = new ConcurrentLinkedQueue<Seed>();
    private int maxSize = 102400;

    public static SeedCollection getInstance() {
        if (null == instance) {
            synchronized (SeedCollection.class) {
                if (null == instance) {
                    instance = new SeedCollection();
                }
            }
        }
        return instance;
    }

    private SeedCollection() {
    }

    public List<Metrics> stat() {
        List<Metrics> stats = new ArrayList<Metrics>();
        stats.add(new Metrics("seedSize", new Integer(size()).toString()));
        return stats;
    }

    public int size() {
        return this.queue.size();
    }

    public List<Seed> getSeeds(int max) {
        List<Seed> result = new ArrayList<Seed>();
        int counter = 0;
        Seed url = null;
        while (((url = this.queue.poll()) != null) && (counter < max)) {
            result.add(url);
        }
        return result;
    }

    public boolean putSeeds(List<Seed> urls) {
        if (null == urls || urls.size() == 0) {
            logger.warn("parameter is invalid");
            return true;
        }
        int queueSize = this.queue.size();
        int addSize = urls.size();
        if (queueSize + addSize >= maxSize) {
            logger.warn("size excceed. queueSize=" + queueSize + ", addSize=" + addSize);
            return false;
        }
        for (Seed url : urls) {
            if (!this.queue.offer(url)) {
                logger.warn("failed to offer element");
                return false;
            }
        }
        return true;
    }
}
