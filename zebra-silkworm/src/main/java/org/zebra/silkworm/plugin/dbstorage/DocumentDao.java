package org.zebra.silkworm.plugin.dbstorage;

import java.util.*;
import java.io.Serializable;

public interface DocumentDao {
	public Serializable save(CommonDocument doc);
	public void update(CommonDocument doc);
	public void delete(CommonDocument doc);
	public CommonDocument loadByUrl(String url);
	public List<CommonDocument> retriveByTime(String time, int begin, int limit);
	public List<CommonDocument> retriveAll(int begin, int limit);
}
