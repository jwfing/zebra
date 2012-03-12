package org.zebra.search.crawler.plugin;

import org.apache.log4j.Logger;
import org.zebra.search.crawler.common.Context;
import org.zebra.search.crawler.common.CrawlDocument;
import org.zebra.search.crawler.common.Processor;
import org.zebra.search.crawler.plugin.dbstorage.DocumentDao;
import org.zebra.search.crawler.plugin.dbstorage.CommonDocument;
import org.zebra.search.crawler.util.ProcessorUtil;

public class DocumentSaver implements Processor {
    private static Logger logger = Logger.getLogger(DocumentSaver.class);
    private DocumentDao documentDao = null;

    public DocumentDao getDocumentDao() {
        return documentDao;
    }

    public void setDocumentDao(DocumentDao documentDao) {
        this.documentDao = documentDao;
    }

    @Override
    public boolean initialize() {
        logger.info("DocumentSave initialized.");
        return true;
    }

    @Override
    public boolean destroy() {
        logger.info("DocumentSave destroy.");
        return true;
    }

    @Override
    public String getName() {
        return this.getClass().getName();
    }

    @Override
    public boolean process(CrawlDocument doc, Context context) {
        if (null == doc || null == context) {
            logger.warn("invalid parameter. document or context is empty.");
            return false;
        }
        if (null == this.documentDao) {
            logger.warn("internal error, documentDao is empty");
            return false;
        }
        CommonDocument document = new CommonDocument();
        String title = (String) context.getVariable(ProcessorUtil.COMMON_PROP_TITLE);
        String description = (String) context.getVariable(ProcessorUtil.COMMON_PROP_DESCRIPTION);
        String articleText = (String) context.getVariable(ProcessorUtil.COMMON_PROP_MAINBODY);
        document.setUrl(doc.getUrl());
        document.setUrlMd5(org.zebra.search.crawler.util.StringUtil.computeMD5(doc.getUrl()));
        document.setSourceUrl((String)doc.getUrlInfo().getFeature(ProcessorUtil.COMMON_PROP_SEEDURL));
        document.setTitle(title);
        document.setDescription(description);
        document.setArticleText(articleText);
        document.setDownloadTime(doc.getFetchTime().getTime());
        logger.debug("save document:" + document.toString());
        this.documentDao.save(document);
        return true;
    }
}
