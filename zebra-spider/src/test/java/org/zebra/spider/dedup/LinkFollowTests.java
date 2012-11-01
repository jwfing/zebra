package org.zebra.spider.dedup;

import java.util.List;
import java.util.ArrayList;

import org.zebra.common.Context;
import org.zebra.common.CrawlDocument;
import org.zebra.common.UrlInfo;
import org.zebra.common.domain.Seed;
import org.zebra.common.flow.plugin.DocumentParser;
import org.zebra.common.http.HttpClientFetcher;
import org.zebra.common.utils.ProcessorUtil;
import org.zebra.spider.plugin.LinkFollower;
import org.zebra.spider.plugin.RulesetFilter;

import junit.framework.TestCase;

public class LinkFollowTests extends TestCase {
    private HttpClientFetcher fetcher = new HttpClientFetcher();
    private static boolean initialized = false;
    protected void setUp() throws Exception {
        super.setUp();
        if (!initialized) {
            HttpClientFetcher.startConnectionMonitorThread();
            initialized = true;
        }
    }

    public void testListPage() {
        List<Seed> seedList = new ArrayList<Seed>();
        Seed mafengwo = new Seed();
        mafengwo.setStrict("http://www\\.mafengwo\\.cn/i/\\d*\\.html");
        mafengwo.setTags("Travel");
        mafengwo.setUrl("http://www.mafengwo.cn");
        Seed xiaohua = new Seed();
        xiaohua.setStrict("http://www\\.5201516\\.com/xiaohua/12/.*\\.html");
        xiaohua.setTags("Humor");
        xiaohua.setUrl("http://www.5201516.com/xiaohua/12");
        Seed hexunshuping = new Seed();
        hexunshuping.setStrict("http://book.hexun.com/\\d+.*\\.html");
        hexunshuping.setTags("Review");
        hexunshuping.setUrl("http://book.hexun.com/prose/");
        seedList.add(mafengwo);
        seedList.add(xiaohua);
        seedList.add(hexunshuping);
        for (Seed seed : seedList) {
            UrlInfo urlInfo = new UrlInfo(seed.getUrl());
            urlInfo.addFeature(ProcessorUtil.COMMON_PROP_TAG, seed.getTags());
            urlInfo.addFeature(ProcessorUtil.COMMON_PROP_STRICT, seed.getStrict());
            urlInfo.addFeature(ProcessorUtil.COMMON_PROP_FLAG, ProcessorUtil.FLAG_VALUE_LIST);
            CrawlDocument doc = null;
            try {
                doc = fetcher.fetchDocument(urlInfo);
            } catch (Exception ex) {
                ex.printStackTrace();
                continue;
            }
            if (null == doc || null == doc.getContentString()) {
                continue;
            }
            Context context = new Context();
            DocumentParser parser = new DocumentParser();
            parser.initialize();
            LinkFollower follower = new LinkFollower();
            follower.initialize();
            RulesetFilter filter = new RulesetFilter();
            filter.initialize();
            boolean result = parser.process(doc, context);
            assert(result != false);
            result = follower.process(doc, context);
            assert(result != false);
            result = filter.process(doc, context);
            assert(result != false);
            List<UrlInfo> outlinks = (List<UrlInfo>)context.getVariable(ProcessorUtil.COMMON_PROP_OUTLINKS);
            System.out.println("compare url:" + seed.getUrl() + ", outlinks:" + outlinks.size());
            for (UrlInfo link: outlinks) {
                System.out.println("\t links: " + link.getUrl());
            }
        }
    }
}
