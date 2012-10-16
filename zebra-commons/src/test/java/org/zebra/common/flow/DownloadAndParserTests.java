package org.zebra.common.flow;

import org.zebra.common.Context;
import org.zebra.common.CrawlDocument;
import org.zebra.common.UrlInfo;
import org.zebra.common.flow.plugin.CharsetConvertor;
import org.zebra.common.flow.plugin.DocumentParser;
import org.zebra.common.http.HttpClientFetcher;

import junit.framework.TestCase;

public class DownloadAndParserTests extends TestCase {
    private HttpClientFetcher fetcher = new HttpClientFetcher();
    private static boolean initialized = false;
    protected void setUp() throws Exception {
        super.setUp();
        if (!initialized) {
            HttpClientFetcher.startConnectionMonitorThread();
            initialized = true;
        }
    }

    public void test125K10() {
        String urls[] = {"http://cn.engadget.com/2012/03/09/microsoft-tango-details/",
                "http://cn.engadget.com/category/features/",
                "http://cn.engadget.com/",
                "http://cn.engadget.com/page/2/",
                "http://cn.engadget.com/category/internet/"};
        for (String url : urls) {
            CrawlDocument doc = null;
            try {
                doc = fetcher.fetchDocument(new UrlInfo(url));
            } catch (Exception ex) {
                ex.printStackTrace();
                continue;
            }
            if (null == doc || null == doc.getContentString()) {
                continue;
            }
            Context context = new Context();
            CharsetConvertor convertor = new CharsetConvertor();
            convertor.initialize();
            DocumentParser parser = new DocumentParser();
            parser.initialize();
            boolean result = convertor.process(doc, context);
            assert(result != false);
            result = parser.process(doc, context);
            assert(result != false);
        }
    }

}
