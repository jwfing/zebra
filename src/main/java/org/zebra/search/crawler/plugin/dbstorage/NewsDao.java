package org.zebra.search.crawler.plugin.dbstorage;

import java.util.*;
import java.io.Serializable;

public interface NewsDao {
	public Serializable save(News news);
	public void update(News news);
	public void delete(News news);
	public News loadByUrl(String url);
	public List<News> retriveByTime(String time, int begin, int limit);
	public List<News> retriveAll(int begin, int limit);
}
