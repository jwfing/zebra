package org.zebra.common.http;

import java.util.concurrent.TimeUnit;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IdleConnectionMonitorThread extends Thread {
    protected Logger logger = LoggerFactory.getLogger(getClass().getName());
    private final ThreadSafeClientConnManager connMgr;
    private volatile boolean shutdown = false;

    public IdleConnectionMonitorThread(ThreadSafeClientConnManager connMgr) {
        super("Connection Manager");
        this.connMgr = connMgr;
    }

    @Override
    public void run() {
        try {
            int counter = 0;
            while (!shutdown) {
                synchronized (this) {
                    wait(60000);
                    counter++;
                    // Close expired connections
                    connMgr.closeExpiredConnections();
                    // Optionally, close connections
                    // that have been idle longer than 30 sec
                    connMgr.closeIdleConnections(300, TimeUnit.SECONDS);
                    if (counter % 10 == 0) {
                        logger.info("[METRICS_STAT] idleConn=" + connMgr.getConnectionsInPool());
                    }
                }
            }
        } catch (InterruptedException ex) {
            // terminate
        }
    }

    public void shutdown() {
        shutdown = true;
        synchronized (this) {
            notifyAll();
        }
    }
}
