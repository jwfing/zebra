package org.zebra.common.http;

import org.zebra.common.*;
import junit.framework.TestCase;

public class HttpClientFetcherTests extends TestCase {
    private HttpClientFetcher fetcher = new HttpClientFetcher();

    public void testBasic() {
        UrlInfo url = new UrlInfo("http://hc.apache.org/httpclient-3.x/tutorial.html");
        CrawlDocument doc = fetcher.fetchDocument(url);
        assert(doc != null);
        assert(doc.getFetchStatus() == FetchStatus.OK);
    }
}
