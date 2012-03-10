package org.zebra.search.crawler.plugin;

import org.zebra.search.crawler.common.Context;
import org.zebra.search.crawler.plugin.CommonArticleExtractor;
import org.zebra.search.crawler.util.ProcessorUtil;

public class ArticleTextExtractorTests extends PluginTestBase {
    public ArticleTextExtractorTests() {
       super("./src/test/resources/sina_145810356592.shtml");
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
