package org.zebra.common.kestrel;

import net.spy.memcached.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;

public class SimpleKestrelQueue {
    Logger logger = LoggerFactory.getLogger(getClass().getName());

    private String kestrelAddress = "";
    private String incomingQueue = "";
    private MemcachedClient client = null;
    private int pollTimeoutMS = 100;

    public String getKestrelAddress() {
        return kestrelAddress;
    }

    public void setKestrelAddress(String kestrelAddress) {
        this.kestrelAddress = kestrelAddress;
    }

    public String getIncomingQueue() {
        return incomingQueue;
    }

    public void setIncomingQueue(String incomingQueue) {
        this.incomingQueue = incomingQueue;
    }

    public int getPollTimeoutMS() {
        return pollTimeoutMS;
    }

    public void setPollTimeoutMS(int pollTimeoutMS) {
        this.pollTimeoutMS = pollTimeoutMS;
    }

    @PostConstruct
    public void init() {
        try {
            logger.info("Initializing SimpleKestrelQueue with address " + kestrelAddress + ", queue: " + incomingQueue);
            if (null == kestrelAddress|| kestrelAddress.isEmpty()) {
                return;
            }
            ConnectionFactoryBuilder builder = new ConnectionFactoryBuilder();
            // We never want spymemcached to time out, 10 days
            builder.setOpTimeout(864000000l);
            // Retry upon failure
            builder.setFailureMode(FailureMode.Retry);
            builder.setShouldOptimize(false); // VERY IMPORTANT! OPTIMIZES AWAY MULTIPLE GETS TO SAME KEY OTHERWISE!
            ConnectionFactory memcachedConnectionFactory = builder.build();
            List<InetSocketAddress> addrs = AddrUtil.getAddresses(kestrelAddress);
            client = new MemcachedClient(memcachedConnectionFactory, addrs);
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    @PreDestroy
    public void destroy() {
        if (null != client) {
            client.shutdown();
        }
    }
    public String dequeue() {
        if (null == client) {
            throw new IllegalStateException("Cannot peform dequeue() operation without specifying kestrel address.");
        }
        if(incomingQueue == null || "".equals(incomingQueue)) {
            throw new IllegalStateException("Cannot peform dequeue() operation without specifying incoming queue name.");
        }
        return (String)client.get(incomingQueue + "/t=" + pollTimeoutMS);
    }

    public void enqueue(String outgoingQueue, String msg) {
        if (null == client) {
            logger.warn("Not initialized SimpleKestrelQueue with correct parameters.");
            return;
        }
        client.set(outgoingQueue, 0, msg);
    }
}
