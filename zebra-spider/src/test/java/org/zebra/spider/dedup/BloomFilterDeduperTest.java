package org.zebra.spider.dedup;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.zebra.common.*;
import org.zebra.common.flow.plugin.*;
import org.zebra.common.utils.ProcessorUtil;
import org.zebra.common.http.*;
import org.zebra.spider.plugin.LinkFollower;

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

    public void test125K10() {
        BloomFilterDeduper deduper = new BloomFilterDeduper(1250000);
        HashDeduper hashDeduper = new HashDeduper();
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
            assert(result != false);
            result = parser.process(doc, context);
            assert(result != false);
            result = follower.process(doc, context);
            assert(result != false);
            List<UrlInfo> outlinks = (List<UrlInfo>)context.getVariable(ProcessorUtil.COMMON_PROP_OUTLINKS);
            System.out.println("compare url:" + url + ", outlinks:" + outlinks.size());
            Map<String, Boolean> resultOfHash = hashDeduper.dedup(outlinks);
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

}
