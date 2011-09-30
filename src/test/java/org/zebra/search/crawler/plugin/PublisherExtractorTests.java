package org.zebra.search.crawler.plugin;

import org.htmlparser.util.NodeList;
import org.zebra.search.crawler.common.Context;
import org.zebra.search.crawler.plugin.extractor.SourceExtractor;
import org.zebra.search.crawler.util.ProcessorUtil;

public class PublisherExtractorTests extends PluginTestBase {
	public PublisherExtractorTests() {
		super("./testdata/sina_145810356592.shtml");
	}

	public void testSinaPage() {
		Context context = new Context();
		DocumentParser parser = new DocumentParser();
		boolean result = parser.process(doc, context);
		if (!result) {
			fail("failed to process htmlparser");
		}
		SourceExtractor extractor = new SourceExtractor();
		NodeList nodeList = (NodeList) context
				.getVariable(ProcessorUtil.COMMON_PROP_NODELIST);
		if (nodeList == null) {
			return;
		}
		System.out.println("source=" + extractor.extract(doc, context));
	}
}
