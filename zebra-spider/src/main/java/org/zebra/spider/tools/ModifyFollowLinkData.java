package org.zebra.spider.tools;

import java.util.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.zebra.common.domain.dao.FollowedLinkDao;
import org.zebra.common.domain.FollowedLink;
import org.zebra.common.utils.StringUtil;
import org.zebra.common.utils.UrlUtil;

public class ModifyFollowLinkData {
    private static final String CONFIG_PATH = "modify_context.xml";

    public static void main(String[] args) {
        ApplicationContext appContext = new ClassPathXmlApplicationContext(CONFIG_PATH);
        FollowedLinkDao dao = (FollowedLinkDao) appContext.getBean("followlinkDAO");
        long now = System.currentTimeMillis() / 1000;
        List<FollowedLink> links = dao.getLinks(0, now, 0, 110000);
        System.out.println("check links size:" + links.size());
        Map<String, FollowedLink> url2links = new HashMap<String, FollowedLink>();
        for (FollowedLink link : links) {
            if (null == link.getTags()) {
                continue;
            }
            String url = link.getUrl();
            if (url.startsWith("http://www.gxdxw.cn")) {
                String normalizedUrl = UrlUtil.canonalizeUrl(url);
                if (!url.equals(normalizedUrl)) {
                    if (!url2links.containsKey(url)) {
                        url2links.put(url, link);
                    } else {
                        FollowedLink other = url2links.get(url);
                        if (other.getId() < link.getId()) {
                            dao.delete(other);
                            System.out.println("delete id=" + other.getId() + ", url=" + url);
                        } else {
                            dao.delete(link);
                            System.out.println("delete id=" + link.getId() + ", url=" + url);
                        }
                    }
                }
            }
        }
    }
}
