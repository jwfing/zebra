package org.zebra.search.crawler.plugin;

import java.util.Map;
import org.apache.log4j.Logger;
import org.zebra.search.crawler.common.Context;
import org.zebra.search.crawler.common.CrawlDocument;
import org.zebra.search.crawler.common.Processor;
import org.zebra.search.crawler.plugin.extractor.ArticleTextExtractor;
import org.zebra.search.crawler.plugin.extractor.HTMLMetaExtractor;
import org.zebra.search.crawler.util.ProcessorUtil;

public class CommonArticleExtractor implements Processor {
    private final Logger logger = Logger.getLogger(CommonArticleExtractor.class);
    private ArticleTextExtractor articleTextExtractor = new ArticleTextExtractor();
    private HTMLMetaExtractor htmlMetaExtractor = new HTMLMetaExtractor();
    public boolean initialize() {
        logger.info("successful initialized " + CommonArticleExtractor.class.getName());
        return true;
    }

    public boolean destroy() {
        logger.info("successful destroied " + CommonArticleExtractor.class.getName());
        return true;
    }

    public String getName() {
        return this.getClass().getName();
    }

    public boolean process(CrawlDocument doc, Context context) {
        if (null == doc || null == context) {
            return false;
        }
        String articleText = this.articleTextExtractor.extract(doc, context);
        Map<String, String> metas = this.htmlMetaExtractor.extract(doc, context);
        context.setVariable(ProcessorUtil.COMMON_PROP_MAINBODY, articleText);
        context.setVariable(ProcessorUtil.COMMON_PROP_TITLE, metas.get(ProcessorUtil.COMMON_PROP_TITLE));
        context.setVariable(ProcessorUtil.COMMON_PROP_DESCRIPTION, metas.get(ProcessorUtil.COMMON_PROP_DESCRIPTION));
        return true;
    }
}
