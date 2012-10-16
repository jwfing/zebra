package org.zebra.common.http;

import java.util.*;
import org.zebra.common.UrlInfo;

public class PoliteFetcher extends HttpClientFetcher {
    protected boolean waitMoment(UrlInfo url) {
        try {
            Thread.sleep(1000);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return true;
    }
}
