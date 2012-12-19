package org.zebra.common.kestrel;

import junit.framework.TestCase;

public class SimpleKestrelQueueTest extends TestCase {
    private SimpleKestrelQueue queue = new SimpleKestrelQueue();

    protected void setUp() throws Exception {
        super.setUp();
        this.queue.setKestrelAddress("127.0.0.1:22133");
        this.queue.setIncomingQueue("newLinks");
        this.queue.init();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        this.queue.destroy();
    }

    public void testEnqueue() {
        try {
            Thread.sleep(1000);
        } catch (Exception ex) {
            ;
        }
        String[] urls = {"http://vip.stock.finance.sina.com.cn/q/go.php/vReport_Show/kind/company/rptid/1648986/index.phtml",
                "http://finance.sina.com.cn/stock/quanshang/qsyj/20121219/033214046052.shtml",
                "http://finance.sina.com.cn/china/20121219/012414044470.shtml"};

        for (String url : urls) {
            this.queue.enqueue("newLinks", url);
            System.out.println("Okay! push url=" + url + " to newLinks");
        }
/*
        for (int i = 0; i < urls.length; i++) {
            String tmp = this.queue.dequeue();
            System.out.println("dequeue: " + tmp);
        }
*/
    }
}
