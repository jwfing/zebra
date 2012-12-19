package org.zebra.silkworm.plugin;

import java.util.List;
import java.util.Date;

import org.apache.log4j.Logger;
import org.zebra.common.Context;
import org.zebra.common.CrawlDocument;
import org.zebra.common.UrlInfo;
import org.zebra.common.flow.Processor;
import org.zebra.common.utils.ProcessorUtil;
import org.zebra.silkworm.dao.NewsDao;
import org.zebra.silkworm.domain.News;

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
    public boolean process(CrawlDocument doc, Context context) {
        if (null == doc || null == context) {
            logger.warn("invalid parameter.");
            return false;
        }
        UrlInfo currentUrlInfo = doc.getUrlInfo();
        News news = new News();
        String title = (String)context.getVariable(ProcessorUtil.COMMON_PROP_ARTICLETITLE);
        String publishTime = (String)context.getVariable(ProcessorUtil.COMMON_PROP_PUBLISHTIME);
        String mainText = (String)context.getVariable(ProcessorUtil.COMMON_PROP_MAINBODY);
        String source = (String)context.getVariable(ProcessorUtil.COMMON_PROP_PUBLISHSOURCE);
        List<String> binaryFiles = (List<String>)context.getVariable(ProcessorUtil.COMMON_PROP_BINARYLINKS);
        news.setMainText(mainText);
        news.setUrl(currentUrlInfo.getUrl());
        news.setPublisher(source);
        news.setTitle(title);
        news.setDownloadTime(new Date().toString());
        news.setPublishTime(publishTime);
        if (null != binaryFiles && binaryFiles.size() > 0) {
            boolean isFirst = true;
            StringBuilder sb = new StringBuilder();
            for (String file : binaryFiles) {
                if (isFirst) {
                    sb.append(file);
                    isFirst = false;
                } else {
                    sb.append("," + file);
                }
            }
            news.setAttachmentPath(sb.toString());
        }
        try {
            this.newsDao.save(news);
        } catch (Exception ex) {
            logger.warn("failed to save News Document. cause:" + ex.getMessage());
            return false;
        }
        return true;
    }

}
