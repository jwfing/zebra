package org.zebra.silkworm.plugin;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.zebra.common.*;
import org.zebra.common.flow.*;
import org.zebra.common.utils.ProcessorUtil;
import org.zebra.silkworm.plugin.extractor.*;

public class NewsElementsExtractor implements Processor {
    private final Logger logger = Logger.getLogger(NewsElementsExtractor.class);
    private ArticleTitleExtractor titleExtractor = new ArticleTitleExtractor();
    private MainTextExtractor mainTextExtractor = new MainTextExtractor();
    private PublishTimeExtractor timeExtractor = new PublishTimeExtractor();
    private SourceExtractor sourceExtractor = new SourceExtractor();

    public boolean initialize() {
        logger.info("successful initialized " + NewsElementsExtractor.class.getName());
        return true;
    }

    public boolean destroy() {
        logger.info("successful destroied " + NewsElementsExtractor.class.getName());
        return true;
    }

    public String getName() {
        return this.getClass().getName();
    }

    public boolean process(CrawlDocument doc, Context context) {
        if (null == doc || null == context) {
            logger.warn("invalid parameter.");
            return false;
        }
        String title = StringEscapeUtils.unescapeHtml(this.titleExtractor.extract(doc, context));
        String time = StringEscapeUtils.unescapeHtml(this.timeExtractor.extract(doc, context));
        String source = StringEscapeUtils.unescapeHtml(this.sourceExtractor.extract(doc, context));
        String mainText = StringEscapeUtils.unescapeHtml(this.mainTextExtractor.extract(doc, context));
        context.setVariable(ProcessorUtil.COMMON_PROP_ARTICLETITLE, title);
        context.setVariable(ProcessorUtil.COMMON_PROP_PUBLISHTIME, time);
        context.setVariable(ProcessorUtil.COMMON_PROP_MAINBODY, mainText);
        context.setVariable(ProcessorUtil.COMMON_PROP_PUBLISHSOURCE, source);
        return true;
    }
}
