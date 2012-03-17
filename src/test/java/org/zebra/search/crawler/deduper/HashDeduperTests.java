package org.zebra.search.crawler.deduper;

import java.io.*;
import java.util.*;

import junit.framework.TestCase;

import org.zebra.search.crawler.common.*;

public class HashDeduperTests extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testCheckpoint() {
        String urlSample = "http://books.solidot.org/books/12/02/17/0223248.shtml";
        String urlNotExist = "http://books.solidot.cnbeta.infoq.com/cnengadat/techweb/index.html";
        String chkFile = "./checkpoint/deduper.cur";
        try {
            HashDeduper deduper1 = new HashDeduper();
            BufferedReader br = new BufferedReader(new FileReader("./crawler.url"));
            String line = null;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }
                UrlInfo urlInfo = new UrlInfo(line);
                List<UrlInfo> list = new ArrayList<UrlInfo>();
                list.add(urlInfo);
                deduper1.dedup(list);
            }
            boolean res = deduper1.checkpoint(chkFile);
            if (!res) {
                fail("failed to checkpoint");
            }
            HashDeduper deduper2 = new HashDeduper();
            res = deduper2.reload(chkFile);
            if (!res) {
                fail("failed to reload");
            }
            List<UrlInfo> testUrls = new ArrayList<UrlInfo>();
            testUrls.add(new UrlInfo(urlSample));
            testUrls.add(new UrlInfo(urlNotExist));
            List<Boolean> result1 = deduper1.juegeDeduped(testUrls);
            List<Boolean> result2 = deduper2.juegeDeduped(testUrls);
            if (result1.size() != result2.size()) {
                fail();
            }
            for (int i = 0; i < result1.size(); ++i) {
                if (!result1.get(i).equals(result2.get(i))) {
                    fail();
                }
                System.out.println("result " + i + " is " + result1.get(i));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("encounter exception. cause:" + ex.getMessage());
        }
    }
}
