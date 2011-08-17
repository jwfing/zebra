package org.zebra.search.crawler.common;

import junit.framework.TestCase;

public class ConfigurationTests extends TestCase {
	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testGeneral() {
		Configuration conf = new Configuration();
        boolean init = conf.initialize("normal.properties");
        if (!init) {
            fail("failed to initialize normal.properties");
        }
        String str = conf.getStringProperty("crawler.urlpool.dir", "");
        if (str.isEmpty()) {
        	fail();
        }
        System.out.println(str);
        int value = conf.getIntProperty("crawler.fetcher.threads", 0);
        if (value == 0) {
        	fail();
        }
        System.out.println(value);
        boolean bvalue = conf.getBooleanProperty("crawler.urlpool.resume", true);
        if (bvalue) {
        	fail();
        }
        System.out.println(bvalue);
	}
}
