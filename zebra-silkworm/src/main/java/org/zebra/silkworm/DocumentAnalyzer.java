package org.zebra.silkworm;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.zebra.common.flow.ProcessorChain;
import org.zebra.common.domain.dao.DocumentDao;

public class DocumentAnalyzer {
    private static final Log LOG = LogFactory.getLog(DocumentAnalyzer.class);
    private ProcessorChain processorChain = null;
    private DocumentDao documentDao = null;

    public ProcessorChain getProcessorChain() {
        return processorChain;
    }

    public void setProcessorChain(ProcessorChain processorChain) {
        this.processorChain = processorChain;
    }

    public DocumentDao getDocumentDao() {
        return documentDao;
    }

    public void setDocumentDao(DocumentDao documentDao) {
        this.documentDao = documentDao;
    }

    public void onNewLink(String link) {
        LOG.info("process link: " + link);
    }
}
