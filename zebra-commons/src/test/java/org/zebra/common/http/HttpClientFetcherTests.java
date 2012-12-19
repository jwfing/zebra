package org.zebra.common.http;

import org.zebra.common.*;
import org.zebra.common.flow.plugin.CharsetConvertor;
import org.zebra.common.flow.plugin.DocumentParser;

import junit.framework.TestCase;

public class HttpClientFetcherTests extends TestCase {
    private HttpClientFetcher fetcher = new HttpClientFetcher();

    public void testBasic() {
        UrlInfo url = new UrlInfo("http://hc.apache.org/httpclient-3.x/tutorial.html");
        CrawlDocument doc = fetcher.fetchDocument(url);
        assert(doc != null);
        assert(doc.getFetchStatus() == FetchStatus.OK);
    }
    public void testConvertor() {
        UrlInfo url = new UrlInfo("http://finance.sina.com.cn/china/20121219/012414044470.shtml");
        CrawlDocument doc = fetcher.fetchDocument(url);
        assert(doc != null);
        assert(doc.getFetchStatus() == FetchStatus.OK);
        Context context = new Context();
        boolean ret =false;
        CharsetConvertor convertor = new CharsetConvertor();
        ret = convertor.process(doc, context);
        assert(ret);
        DocumentParser parser = new DocumentParser();
        ret = parser.process(doc, context);
        assert(ret);
    }
}
