package org.zebra.search.crawler.deduper;

import java.util.*;

import org.zebra.search.crawler.common.*;

public interface Deduper {
    public Map<String, Boolean> dedup(List<UrlInfo> urls);
    public List<Boolean> juegeDeduped(List<UrlInfo> urls);
    public boolean deleteInvalidUrl(List<UrlInfo> urls);
    public boolean isFull();
    public void clear();
    public boolean checkpoint(String fileName);
    public boolean reload(String fileName);
}
