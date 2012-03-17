package org.zebra.search.crawler.deduper;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.zebra.search.crawler.common.Context;
import org.zebra.search.crawler.common.CrawlDocument;
import org.zebra.search.crawler.common.UrlInfo;
import org.zebra.search.crawler.fetcher.HttpClientFetcher;
import org.zebra.search.crawler.plugin.CharsetConvertor;
import org.zebra.search.crawler.plugin.DocumentParser;
import org.zebra.search.crawler.plugin.LinkFollower;
import org.zebra.search.crawler.util.ProcessorUtil;

import junit.framework.TestCase;

public class BloomFilterDeduperTest extends TestCase {
    private HttpClientFetcher fetcher = new HttpClientFetcher();
    private static boolean initialized = false;
    protected void setUp() throws Exception {
        super.setUp();
        if (!initialized) {
            HttpClientFetcher.startConnectionMonitorThread();
            initialized = true;
        }
    }
    /*
    public void test125K10() {
        BloomFilterDeduper deduper = new BloomFilterDeduper(1250000);
        HashDeduper hash = new HashDeduper();
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
            LinkFollower follower = new LinkFollower();
            follower.initialize();
            boolean result = convertor.process(doc, context);
            if (!result) {
                continue;
            }
            result = parser.process(doc, context);
            if (!result) {
                continue;
            }
            result = follower.process(doc, context);
            if (!result) {
                continue;
            }
            List<UrlInfo> outlinks = (List<UrlInfo>)context.getVariable(ProcessorUtil.COMMON_PROP_OUTLINKS);
            System.out.println("compare url:" + url + ", outlinks:" + outlinks.size());
            Map<String, Boolean> resultOfHash = hash.dedup(outlinks);
            Map<String, Boolean> resultOfBloom = deduper.dedup(outlinks);
            Set<String> allKeys = resultOfHash.keySet();
            for (String key: allKeys) {
                if (!resultOfHash.get(key).equals(resultOfBloom.get(key))) {
                    System.out.println("key:" + key + ", resultOfHash:" + resultOfHash.get(key) + ", resultOfBloom:" + resultOfBloom.get(key));
                    fail("dedup failed.");
                } else {
                    System.out.println("pass key:" + key);
                }
            }
        }
    }
    */
}
