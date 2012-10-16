package org.zebra.spider.plugin;

import java.io.File;
import java.util.List;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.zebra.spider.dedup.*;
import org.zebra.spider.Constants;
import org.zebra.common.utils.ProcessorUtil;
import org.zebra.common.*;
import org.zebra.common.flow.*;

public class DeduperClient implements Processor {
    private static final String CURRENT_CHECKPOINT =
            Configuration.getStringProperty(Constants.PATH_DEDUP_CHECKPOINT, "./checkpoint/deduper.cur");
    private static final String BACKUP_CHECKPOINT =
            Configuration.getStringProperty(Constants.PATH_DEDUP_CHECKPOINT_BAK, "./checkpoint/deduper.backup");
    private static final String TMP_CHECKPOINT =
            Configuration.getStringProperty(Constants.PATH_DEDUP_CHECKPOINT_TMP, "./checkpoint/deduper.tmp");

    private final Logger logger = Logger.getLogger(DeduperClient.class);
    private Deduper deduper = new HashDeduper();
    private CheckPointThread checkPointThread = null;

    private class CheckPointThread extends Thread {
        private int intervalMinutes = 5;

        public void run() {
            while (isAlive()) {
                File tmpFile = new File(TMP_CHECKPOINT);
                if (tmpFile.exists()) {
                    tmpFile.delete();
                }
                boolean writeResult = deduper.checkpoint(TMP_CHECKPOINT);
                if (writeResult) {
                    File curFile = new File(CURRENT_CHECKPOINT);
                    if (curFile.exists()) {
                        curFile.renameTo(new File(BACKUP_CHECKPOINT));
                    }
                    tmpFile = new File(TMP_CHECKPOINT);
                    tmpFile.renameTo(curFile);
                    logger.info("successfully generated a checkpoint for HashDeduper");
                }
                try {
                    sleep(this.intervalMinutes * 60000);
                } catch (Exception ex) {
                    logger.warn("exception encountered. cause:" + ex.getMessage());
                }
            }
        }
    }

    public Deduper getDeduper() {
        return deduper;
    }

    public void setDeduper(Deduper deduper) {
        this.deduper = deduper;
    }

    public boolean initialize() {
        File curFile = new File(CURRENT_CHECKPOINT);
        if (curFile.exists()) {
            this.deduper.reload(CURRENT_CHECKPOINT);
        } else {
            curFile = new File(BACKUP_CHECKPOINT);
            if (curFile.exists()) {
                this.deduper.reload(BACKUP_CHECKPOINT);
            }
        }
        this.checkPointThread = new CheckPointThread();
        this.checkPointThread.start();
        logger.info("initialized " + this.getName());
        return true;
    }

    public boolean destroy() {
        if (null != this.checkPointThread) {
            try {
                this.checkPointThread.join(3000);
            } catch (Exception ex) {
                logger.warn("failed to join checkpoint thread. cause:" + ex.getMessage());
            }
        }
        logger.info("destroyed " + getName());
        return true;
    }

    public String getName() {
        return this.getClass().getName();
    }

    public boolean process(CrawlDocument doc, Context context) {
        if (null != doc && null != context && null != this.deduper) {
            List<UrlInfo> outlinks = (List<UrlInfo>) context
                    .getVariable(ProcessorUtil.COMMON_PROP_OUTLINKS);
            if (outlinks != null && outlinks.size() > 0) {
                List<Boolean> judgeResult = this.deduper.juegeDeduped(outlinks);
                this.deduper.dedup(outlinks);
                if (judgeResult != null && judgeResult.size() == outlinks.size()) {
                    List<UrlInfo> reallinks = new ArrayList<UrlInfo>();
                    for (int i = 0; i < outlinks.size(); i++) {
                        if (!judgeResult.get(i)) {
                            reallinks.add(outlinks.get(i));
                        }
                    }
                    context.setVariable(ProcessorUtil.COMMON_PROP_OUTLINKS, reallinks);
                    logger.info("dedup new link from " + outlinks.size() + " to "
                            + reallinks.size() + " for document=" + doc.getUrl());
                } else {
                    logger.warn("deduper client is invalid");
                }
            } else {
                logger.debug("doc(" + doc.getUrl() + ") has no outlinks");
            }
        } else {
            logger.warn("interval error.");
        }
        return true;
    }
}
