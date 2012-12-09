package org.zebra.silkworm;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.zebra.common.Context;
import org.zebra.common.CrawlDocument;
import org.zebra.common.FetchStatus;
import org.zebra.common.UrlInfo;
import org.zebra.common.flow.ProcessorChain;
import org.zebra.common.http.HttpClientFetcher;
import org.zebra.common.utils.ProcessorUtil;
import org.zebra.common.domain.dao.DocumentDao;

public class DocumentAnalyzer {
    private static final Log LOG = LogFactory.getLog(DocumentAnalyzer.class);
    private ProcessorChain processorChain = null;
    private DocumentDao documentDao = null;

    public ProcessorChain getProcessorChain() {
        return processorChain;
    }

    public void setProcessorChain(ProcessorChain processorChain) {
        LOG.info("set ProcessorChain with processorNum=" + processorChain.size());
        this.processorChain = processorChain;
    }

    public DocumentDao getDocumentDao() {
        return documentDao;
    }

    public void setDocumentDao(DocumentDao documentDao) {
        LOG.info("set Document Dao.");
        this.documentDao = documentDao;
    }

    public void initialize() {
        HttpClientFetcher.startConnectionMonitorThread();
        LOG.info("initialized DocumentAnalyzer");
    }

    public void destroy() {
        HttpClientFetcher.stopConnectionMonitorThread();
        LOG.info("destroied DocumentAnalyzer");
    }

    public void onNewLink(String link) {
        HttpClientFetcher fetcher = new HttpClientFetcher();
        UrlInfo urlInfo = new UrlInfo(link);
        urlInfo.addFeature(ProcessorUtil.COMMON_PROP_FLAG, ProcessorUtil.FLAG_VALUE_CONTENT);
        CrawlDocument doc = fetcher.fetchDocument(urlInfo);
        if (doc.getFetchStatus() != FetchStatus.OK) {
            LOG.warn("failed to fetch document. url=" + link + ", fetchStatus=" + doc.getFetchStatus());
        } else {
            Context context = new Context();
            boolean result = this.processorChain.process(doc, context);
            LOG.info("process link: " + link + ", result: " + result);
        }
    }
}
