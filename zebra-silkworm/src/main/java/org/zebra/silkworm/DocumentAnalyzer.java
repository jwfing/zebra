package org.zebra.silkworm;

import org.zebra.common.flow.ProcessorChain;
import org.zebra.common.domain.dao.DocumentDao;

public class DocumentAnalyzer {
    private ProcessorChain processorChain = null;
    private DocumentDao documentDao = null;

    public void onNewLink(String link) {
        ;
    }
}
