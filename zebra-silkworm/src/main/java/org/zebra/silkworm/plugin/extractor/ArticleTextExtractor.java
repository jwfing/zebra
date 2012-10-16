package org.zebra.silkworm.plugin.extractor;

import java.io.ByteArrayInputStream;
import java.net.URL;

import org.zebra.search.crawler.common.Context;
import org.zebra.search.crawler.common.CrawlDocument;
import org.zebra.search.crawler.util.ProcessorUtil;

import org.apache.log4j.Logger;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.document.TextDocument;
import de.l3s.boilerpipe.extractors.ArticleExtractor;
import de.l3s.boilerpipe.sax.BoilerpipeSAXInput;

public class ArticleTextExtractor {
    private final Logger logger = Logger.getLogger(ArticleTextExtractor.class);
    public String extract(CrawlDocument doc, Context context) {
        try {
            byte[] oldContentBytes = (byte[])context.getVariable(ProcessorUtil.COMMON_PROP_OLDCONTENT);
            if (null == oldContentBytes) {
                oldContentBytes = doc.getContentBytes();
            }
            InputSource is = new InputSource(new ByteArrayInputStream(oldContentBytes));
            BoilerpipeSAXInput in = new BoilerpipeSAXInput(is);
            TextDocument textDoc = in.getTextDocument();
            return ArticleExtractor.INSTANCE.getText(textDoc);
        } catch (SAXException ex) {
            logger.warn("failed to parsing document. SAXException occurred. cause:" + ex.getMessage());
            return null;
        } catch (BoilerpipeProcessingException ex) {
            logger.warn("failed to parsing document. BoilerpipeProcessingException occurred. cause:" + ex.getMessage());
            return null;
        } catch (Exception ex) {
            logger.warn("failed to parsing document. cause:" + ex.getMessage());
            return null;
        }
    }
}
