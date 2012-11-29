package org.zebra.silkworm.plugin;

import org.apache.log4j.Logger;
import org.zebra.common.Context;
import org.zebra.common.CrawlDocument;
import org.zebra.common.flow.Processor;
import org.zebra.silkworm.dao.NewsDao;

public class NewsPageSaver implements Processor {
    private final Logger logger = Logger.getLogger(NewsPageSaver.class);

    private NewsDao newsDao = null;

    public void setNewsDao(NewsDao dao) {
        this.newsDao = dao;
    }

    public NewsDao getNewsDao() {
        return this.newsDao;
    }

    @Override
    public boolean destroy() {
        logger.info("successful destroied " + getName());
        return true;
    }

    @Override
    public String getName() {
        return this.getClass().getName();
    }

    @Override
    public boolean initialize() {
        logger.info("successful initialize " + getName());
        return true;
    }

    @Override
    public boolean process(CrawlDocument arg0, Context arg1) {
        return true;
    }

}
