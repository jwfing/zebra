package org.zebra.search.crawler.urlPool.storage;

import org.apache.log4j.Logger;

import com.sleepycat.je.Cursor;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.OperationStatus;
import com.sleepycat.je.Transaction;

public final class InProcessPagesDB extends WorkQueues {
	private static final Logger logger = Logger.getLogger(InProcessPagesDB.class.getName());
	
	public InProcessPagesDB(Environment env) throws DatabaseException {
		super(env, "InProcessPagesDB", false);
		long docCount = getLength();
		if (docCount > 0) {
			logger.info("Loaded " + docCount + " URLs that have been in process in the previous crawl.");
		}
	}

	public boolean removeURL(UrlSeed webUrl) {
		synchronized (mutex) {
			try {
				DatabaseEntry key = new DatabaseEntry(webUrl.getUrl().getBytes());				
				Cursor cursor = null;
				OperationStatus result = null;
				DatabaseEntry value = new DatabaseEntry();
				Transaction txn = null;//env.beginTransaction(null, null);
				try {
					cursor = urlsDB.openCursor(txn, null);
					result = cursor.getSearchKey(key, value, null);
					
					if (result == OperationStatus.SUCCESS) {
						result = cursor.delete();
						if (result == OperationStatus.SUCCESS) {
							return true;
						}
					}
				} catch (DatabaseException e) {
					if (txn != null) {
						txn.abort();
						txn = null;
					}
					throw e;
				} finally {
					if (cursor != null) {
						cursor.close();
					}
					if (txn != null) {
						txn.commit();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

}
