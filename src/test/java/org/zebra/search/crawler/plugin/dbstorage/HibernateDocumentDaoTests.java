package org.zebra.search.crawler.plugin.dbstorage;

import static org.zebra.search.crawler.plugin.dbstorage.HibernateTestUtil.createMySqlAnnotationConfiguration;

import java.nio.charset.Charset;
import java.util.List;

import org.hibernate.cfg.AnnotationConfiguration;
import org.springframework.orm.hibernate3.HibernateTemplate;

import junit.framework.TestCase;

public class HibernateDocumentDaoTests extends TestCase {
    private HibernateDocumentDao dao;

    protected void setUp() throws Exception {
        AnnotationConfiguration config = createMySqlAnnotationConfiguration();
        config.addAnnotatedClass(CommonDocument.class);
        HibernateTemplate hibernateTemplate = new HibernateTemplate(config.buildSessionFactory());
        this.dao = new HibernateDocumentDao();
        this.dao.setHibernateTemplate(hibernateTemplate);
    }

    protected void tearDown() throws Exception {
        this.dao = null;
    }
/*
    public void testAdd() {
        CommonDocument doc = new CommonDocument();
        doc.setUrl("http://www.sina.com/sports/afhieahfoeafoe.html");
        doc.setSourceUrl("http://www.sina.com/sports/index.html");
        doc.setUrlMd5("fhaeifhaeirfy398ru32orlkfkiefje");
        doc.setArticleText("this is article text");
        doc.setDescription("this is description");
        doc.setTitle("this is title");
        doc.setDownloadTime(332872493);
        this.dao.save(doc);
    }
*/
    public void testList() {
        List<CommonDocument> results = this.dao.retriveAll(0, 100);
        System.out.println("result size:" + results.size());
        for (CommonDocument doc : results) {
            System.out.println(doc.getTitle());
        }
    }
}
