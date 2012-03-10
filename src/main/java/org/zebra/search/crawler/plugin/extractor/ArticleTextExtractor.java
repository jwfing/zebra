package org.zebra.search.crawler.plugin.extractor;

import java.io.ByteArrayInputStream;

import org.zebra.search.crawler.common.Context;
import org.zebra.search.crawler.common.CrawlDocument;

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
            InputSource is = new InputSource(new ByteArrayInputStream(doc.getContentBytes()));
            BoilerpipeSAXInput in = new BoilerpipeSAXInput(is);
            TextDocument textDoc = in.getTextDocument();
            return ArticleExtractor.INSTANCE.getText(textDoc);
        } catch (SAXException ex) {
            logger.warn("failed to parsing document. SAXException occurred. cause:" + ex.getMessage());
            return null;
        } catch (BoilerpipeProcessingException ex) {
            logger.warn("failed to parsing document. BoilerpipeProcessingException occurred. cause:" + ex.getMessage());
            return null;
        }
    }
}
