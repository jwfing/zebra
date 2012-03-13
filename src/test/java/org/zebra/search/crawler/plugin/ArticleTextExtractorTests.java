package org.zebra.search.crawler.plugin;

import org.zebra.search.crawler.common.Context;
import org.zebra.search.crawler.common.CrawlDocument;
import org.zebra.search.crawler.fetcher.HttpClientFetcher;
import org.zebra.search.crawler.plugin.CommonArticleExtractor;
import org.zebra.search.crawler.util.ProcessorUtil;
import org.zebra.search.crawler.common.UrlInfo;

public class ArticleTextExtractorTests extends PluginTestBase {
    private HttpClientFetcher fetcher = new HttpClientFetcher();
    private static boolean initialized = false;
    protected void setUp() throws Exception {
        super.setUp();
        if (!initialized) {
            HttpClientFetcher.startConnectionMonitorThread();
            initialized = true;
        }
    }

    public ArticleTextExtractorTests() {
       super("./src/test/resources/sina_145810356592.shtml");
    }
    public void testEngadgetPage() {
        String url = "http://cn.engadget.com/2012/03/09/microsoft-tango-details/";
        CrawlDocument doc = fetcher.fetchDocument(new UrlInfo(url));
        if (null == doc || null == doc.getContentString()) {
            fail("failed to fetch document");
        }
        Context context = new Context();
        CharsetConvertor convertor = new CharsetConvertor();
        convertor.initialize();
        DocumentParser parser = new DocumentParser();
        parser.initialize();
        CommonArticleExtractor extractor = new CommonArticleExtractor();
        boolean result = convertor.process(doc, context);
        if (!result) {
            fail("failed to convert charset");
        }
        result = parser.process(doc, context);
        if (!result) {
            fail("failed to process htmlparser");
        }
        result = extractor.process(doc, context);
        if (!result) {
            fail("failed to extract article text");
        }
        String text = (String)context.getVariable(ProcessorUtil.COMMON_PROP_MAINBODY);
        System.out.println("text=" + text);
        text = (String)context.getVariable(ProcessorUtil.COMMON_PROP_DESCRIPTION);
        System.out.println("description=" + text);
        text = (String)context.getVariable(ProcessorUtil.COMMON_PROP_TITLE);
        System.out.println("title=" + text);
    }

    public void testSinaPage() {
        Context context = new Context();
        CommonArticleExtractor extractor = new CommonArticleExtractor();
        DocumentParser parser = new DocumentParser();
        boolean result = parser.process(doc, context);
        if (!result) {
            fail("failed to process htmlparser");
        }
        result = extractor.process(doc, context);
        if (!result) {
            fail("failed to process CommonArticleExtractor");
        }
        String text = (String)context.getVariable(ProcessorUtil.COMMON_PROP_MAINBODY);
        System.out.println("Charset=" + ProcessorUtil.getEncoding(text) + ", text=" + text);
    }
    public void testSinaPageWithCharsetConvert() {
        Context context = new Context();
        CommonArticleExtractor extractor = new CommonArticleExtractor();
        DocumentParser parser = new DocumentParser();
        CharsetConvertor convertor = new CharsetConvertor();
        boolean result = convertor.process(doc, context);
        if (!result) {
            fail("failed to process charset convert");
        }
        result = parser.process(doc, context);
        if (!result) {
            fail("failed to process htmlparser");
        }
        result = extractor.process(doc, context);
        if (!result) {
            fail("failed to process CommonArticleExtractor");
        }
        String text = (String)context.getVariable(ProcessorUtil.COMMON_PROP_MAINBODY);
        System.out.println("Charset=" + ProcessorUtil.getEncoding(text) + ", text=" + text);
    }

}
