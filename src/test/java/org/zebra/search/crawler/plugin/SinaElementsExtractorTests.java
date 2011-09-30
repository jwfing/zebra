package org.zebra.search.crawler.plugin;

import org.htmlparser.util.NodeList;
import org.zebra.search.crawler.common.Context;
import org.zebra.search.crawler.common.UrlInfo;
import org.zebra.search.crawler.util.ProcessorUtil;

public class SinaElementsExtractorTests extends PluginTestBase {
	public SinaElementsExtractorTests() {
		super("./testdata/sina_145810356592.shtml");
	}

	public void testSinaPage() {
		this.doc.setUrlInfo(new UrlInfo("http://finance.sina.com.cn/stock/s/20110909/124310460238.shtml"));
		Context context = new Context();
		DocumentParser parser = new DocumentParser();
		boolean result = parser.process(doc, context);
		if (!result) {
			fail("failed to process htmlparser");
		}
		NewsElementsExtractor extractor = new NewsElementsExtractor();
		NodeList nodeList = (NodeList) context
				.getVariable(ProcessorUtil.COMMON_PROP_NODELIST);
		if (nodeList == null) {
			return;
		}
		result = extractor.process(doc, context);
		System.out.println("process result:" + result);
		System.out.println("title=" + 
				context.getVariable(ProcessorUtil.COMMON_PROP_ARTICLETITLE));
		System.out.println("time=" + 
				context.getVariable(ProcessorUtil.COMMON_PROP_PUBLISHTIME));
		System.out.println("body=" + 
		   context.getVariable(ProcessorUtil.COMMON_PROP_MAINBODY));
		System.out.println("source=" + 
		   context.getVariable(ProcessorUtil.COMMON_PROP_PUBLISHSOURCE));
		
	}
}
