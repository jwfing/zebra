package org.zebra.search.crawler.util;

import junit.framework.TestCase;

public class UrlUtilTests extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testGenURL() {
        try {
            String base = "http://www.infoq.com/cn/html5topic";
            String sub1 = "/cn/html-5";
            String sub2 = "../../html-5";
            String result1 = UrlUtil.getAbsoluteUrl(base, sub1);
            String result2 = UrlUtil.getAbsoluteUrl(base, sub2);
            System.out.println(result1);
            System.out.println(result2);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("");
        }
    }
}
