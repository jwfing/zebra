package org.zebra.search.crawler.urlPool.storage;

import java.io.File;
import java.util.List;
import java.util.ArrayList;

import org.zebra.search.crawler.common.UrlInfo;
import org.zebra.search.crawler.common.Configuration;
import org.zebra.search.crawler.urlPool.*;
import org.zebra.search.crawler.util.IOUtil;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;

public class BDBStorageImpl implements UrlStorage {
	private static final Logger logger = Logger.getLogger(BDBStorageImpl.class
			.getName());

	private Environment env = null;
	private WorkQueues repeatUrlDB = null;
	private WorkQueues onceUrlDB = null;

	private static String urlDir = Configuration.getStringProperty(
			Configuration.PATH_URLPOOL_DIR, "./urlPool");
	private static boolean isResume = Configuration.getBooleanProperty(
			Configuration.PATH_URLPOOL_RESUME, true);

	public void initialize() {
		try {
			File envHome = new File(urlDir);
			if (!envHome.exists()) {
				envHome.mkdirs();
			}
			EnvironmentConfig envConfig = new EnvironmentConfig();
			envConfig.setAllowCreate(true);
			envConfig.setTransactional(isResume);
			envConfig.setLocking(isResume);

			if (!isResume) {
				IOUtil.deleteFolderContents(envHome);
			}

			this.env = new Environment(envHome, envConfig);
			this.repeatUrlDB = new WorkQueues(this.env, "RepeatURLsDB",
					isResume);
			this.onceUrlDB = new WorkQueues(this.env, "OnceURLsDB", isResume);
			logger.info("initialize urlPool with directory=" + urlDir
					+ ", resume=" + isResume);
		} catch (DatabaseException e) {
			logger.error("Error while initializing the urlPool(Berkerly DB Storage Impl): "
					+ e.getMessage());
			e.printStackTrace();
			destroy();
		}
	}

	public void destroy() {
		try {
			if (null != this.repeatUrlDB) {
				this.repeatUrlDB.close();
				this.repeatUrlDB = null;
			}
			if (null != this.onceUrlDB) {
				this.onceUrlDB.close();
				this.onceUrlDB = null;
			}
			if (null != this.env) {
				this.env.cleanLog();
				this.env.close();
				this.env = null;
			}
		} catch (DatabaseException e) {
			logger.error("Error while initializing the urlPool(Berkerly DB Storage Impl): "
					+ e.getMessage());
			e.printStackTrace();
		}
	}

	public boolean addRepeatUrls(List<UrlInfo> urls, int level) {
		UrlSeed seed = null;
		for (UrlInfo url : urls) {
			seed = UrlSeed.generateInstanceFromUrlInfo(url, level);
			if (null == seed) {
				logger.warn("failed to convert urlSeed via urlInfo");
				continue;
			}
			if (null != this.repeatUrlDB) {
				try {
					this.repeatUrlDB.put(seed);
					logger.info("add repeat url=" + seed.getUrl() + ", level=" + seed.getLevel());
				} catch (DatabaseException e) {
					e.printStackTrace();
					return false;
				}
			}
		}
		return true;
	}

	public boolean addOnceUrls(List<UrlInfo> urls) {
		UrlSeed seed = null;
		for (UrlInfo url : urls) {
			seed = UrlSeed.generateInstanceFromUrlInfo(url, -1);
			if (null == seed) {
				continue;
			}
			if (null != this.onceUrlDB) {
				try {
					this.onceUrlDB.put(seed);
				} catch (DatabaseException e) {
					e.printStackTrace();
					return false;
				}
			}
		}
		return true;
	}

	public List<UrlInfo> selectFromRepeatUrls(int level, int maxCount) {
		if (null != this.repeatUrlDB) {
			try {
				List<UrlSeed> us = this.repeatUrlDB.get(maxCount);
				logger.debug("get total " + us.size() + " urls from repeatDB");
				List<UrlSeed> target = new ArrayList<UrlSeed>();
				for (UrlSeed seed : us) {
					if (seed.getLevel() != level) {
						logger.debug("level is not match. srcUrl=" + seed.getUrl()
								+ ", srcLevel=" + seed.getLevel() + ", targetLevel=" + level);
						continue;
					}
					target.add(seed);
				}
				return convert2Infos(target);
			} catch (DatabaseException e) {
				e.printStackTrace();
				return null;
			}
		} else {
			logger.info("repeatUrlDB instance is null.");
		}
		return null;
	}

	public List<UrlInfo> selectFromOnceUrls(int maxCount) {
		if (null != this.onceUrlDB) {
			try {
				List<UrlSeed> us = this.onceUrlDB.get(maxCount);
				return convert2Infos(us);
			} catch (DatabaseException e) {
				e.printStackTrace();
				return null;
			}

		}
		return null;
	}

	public boolean dropUrls(List<UrlInfo> urls, Constants.UrlType type) {
		if (Constants.UrlType.ONCE == type) {
			if (null != this.onceUrlDB) {
				for (UrlInfo url : urls) {
					try {
						this.onceUrlDB.delete(convert2Seed(url));
					} catch (DatabaseException e) {
						e.printStackTrace();
					}

				}
				return true;
			}
		} else if (Constants.UrlType.REPEAT == type) {
			if (null != this.repeatUrlDB) {
				for (UrlInfo url : urls) {
					try {
						this.repeatUrlDB.delete(convert2Seed(url));
					} catch (DatabaseException e) {
						e.printStackTrace();
					}

				}
				return true;
			}
		}

		return false;
	}

	private UrlSeed convert2Seed(UrlInfo info) {
		return UrlSeed.generateInstanceFromUrlInfo(info, 0);
	}

	private UrlInfo convert2Info(UrlSeed seed) {
		return UrlSeed.convert2Info(seed);
	}

	private List<UrlInfo> convert2Infos(List<UrlSeed> seeds) {
		List<UrlInfo> infos = new ArrayList<UrlInfo>();
		if (null != seeds) {
			for (UrlSeed seed : seeds) {
				infos.add(convert2Info(seed));
			}
		}
		return infos;
	}
}
