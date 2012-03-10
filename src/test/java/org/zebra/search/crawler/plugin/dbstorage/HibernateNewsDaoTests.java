package org.zebra.search.crawler.plugin.dbstorage;

import static org.zebra.search.crawler.plugin.dbstorage.HibernateTestUtil.createMySqlAnnotationConfiguration;

import java.nio.charset.Charset;

import org.hibernate.cfg.AnnotationConfiguration;
import org.springframework.orm.hibernate3.HibernateTemplate;

import junit.framework.TestCase;

public class HibernateNewsDaoTests extends TestCase {
    private HibernateNewsDao dao;

    protected void setUp() throws Exception {
        AnnotationConfiguration config = createMySqlAnnotationConfiguration();
        config.addAnnotatedClass(News.class);
        HibernateTemplate hibernateTemplate = new HibernateTemplate(config.buildSessionFactory());
        this.dao = new HibernateNewsDao();
        this.dao.setHibernateTemplate(hibernateTemplate);
    }

    protected void tearDown() throws Exception {
        this.dao = null;
    }

    public void testCRUD() {
        News news = new News();
        news.setUrl("http://www.sina.com/finance/test.html");
        String dlSource = new String("新浪网");
        String mainText = "上市公司公告-吉林大化Q2收益持续下降";
        String title = "上市公司公告";
        try {
            Long id = (Long) this.dao.save(news);
            news.setDownloadSource(dlSource);
            news.setMainText(mainText);
            news.setTitle(title);
            this.dao.update(news);
            News two = this.dao.loadByUrl("http://www.sina.com/finance/test.html");
            if (two.getId() != id) {
                fail("load is incorrect");
            }
            System.out.println(title);
            System.out.println(two.getTitle());
            if (!two.getTitle().equals(title)) {
                fail("load bean is not the one exspected");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
