package org.zebra.common.domain.dao;

import java.util.*;
import java.io.Serializable;
import org.zebra.common.domain.*;

public interface DocumentDao {
	public Serializable save(Document doc);
	public void update(Document doc);
	public void delete(Document doc);
	public Document loadByUrl(String url);
	public List<Document> retriveByTime(String time, int begin, int limit);
	public List<Document> retriveAll(int begin, int limit);
}
