package org.zebra.search.crawler.urlPool.storage;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;

import org.zebra.search.crawler.util.IOUtil;

import java.io.File;

import junit.framework.TestCase;

public class InProgressDBTests extends TestCase{
	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testGeneral() {
		boolean resumable = false;
		String storageFolder = "./urlPool";
		try {
			System.out.println(System.getProperty("user.dir").replace("\\", "/"));
			File envHome = new File(storageFolder);
			if (!envHome.exists()) {
				envHome.mkdirs();
			}

			EnvironmentConfig envConfig = new EnvironmentConfig();
			envConfig.setAllowCreate(true);
			envConfig.setTransactional(resumable);
			envConfig.setLocking(resumable);

			if (!resumable) {
				IOUtil.deleteFolderContents(envHome);
			}

			Environment env = new Environment(envHome, envConfig);
			InProcessPagesDB db = new InProcessPagesDB(env);
			UrlSeed seed1 = new UrlSeed("http://a.b.c/e/list.html", 0, "");
			UrlSeed seed2 = new UrlSeed("http://a.b.d/e/list.html", 1, "");
			UrlSeed seed3 = new UrlSeed("http://news.sina.com.cn/e/list.html", 2, "");
			UrlSeed seed4 = new UrlSeed("http://news.sina.com.cn/world/list.html", 3, "");
			db.put(seed1);
			db.put(seed2);
			db.put(seed3);
			db.put(seed4);
			System.out.println("size:" + db.getLength());
			db.removeURL(seed3);
			db.delete(seed2);
			System.out.println("size:" + db.getLength());
			db.close();
			env.cleanLog();
			env.close();
		} catch (DatabaseException dbe) {
		    // 错误处理
			dbe.printStackTrace();
			fail("db open failed");
		}
	}
}
