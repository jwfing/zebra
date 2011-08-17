package org.zebra.search.crawler.plugin;

import java.util.List;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.zebra.search.crawler.deduper.*;
import org.zebra.search.crawler.util.ProcessorUtil;
import org.zebra.search.crawler.common.*;

public class DeduperClient implements Processor{
	private final Logger logger = Logger.getLogger(DeduperClient.class);
    private Deduper deduper = new HashDeduper();

	public Deduper getDeduper() {
		return deduper;
	}

	public void setDeduper(Deduper deduper) {
		this.deduper = deduper;
	}

	public boolean initialize() {
		return true;
	}

	public boolean destroy() {
		return true;
	}
	public String getName() {
		return this.getClass().getName();
	}

	public boolean process(CrawlDocument doc, Context context) {
		if (null != doc && null != context && null != this.deduper) {
			List<UrlInfo> outlinks = (List<UrlInfo>)context.getVariable(ProcessorUtil.COMMON_PROP_OUTLINKS);
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
