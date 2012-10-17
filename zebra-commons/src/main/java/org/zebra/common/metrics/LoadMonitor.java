package org.zebra.common.metrics;

import java.util.ArrayList;
import java.util.List;

public class LoadMonitor implements MetricsReporter {

    @Override
    public List<Metrics> stat() {
        long freeMemory = Runtime.getRuntime().freeMemory();
        long totalMemory = Runtime.getRuntime().totalMemory();
        long maxMemory = Runtime.getRuntime().maxMemory();
        List<Metrics> stats = new ArrayList<Metrics>();
        stats.add(new Metrics("freeMemory", new Long(freeMemory).toString()));
        stats.add(new Metrics("totalMemory", new Long(totalMemory).toString()));
        stats.add(new Metrics("maxMemory", new Long(maxMemory).toString()));
        return stats;
    };

}
