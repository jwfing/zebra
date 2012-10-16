package org.zebra.common.domain.dao;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.zebra.common.domain.Document;

public class HibernateDocumentDao extends HibernateDaoSupport implements DocumentDao {
    private static final Log LOG = LogFactory.getLog(HibernateDocumentDao.class);

    public Serializable save(Document doc) {
        return getHibernateTemplate().save(doc);
    }

    public void update(Document doc) {
        getHibernateTemplate().merge(doc);
    }

    public void delete(Document doc) {
        getHibernateTemplate().delete(doc);
    }

    @SuppressWarnings("unchecked")
    public Document loadByUrl(String url) {
        List<Document> result = new ArrayList<Document>();
        Session session = getHibernateTemplate().getSessionFactory().openSession();
        try {
            Query query = session.createQuery("from CommonDocument where url=:url");
            query.setParameter("url", url);
            result = query.list();
            session.flush();
        } catch (Exception ex) {
            LOG.warn(ex.getMessage());
        } finally {
            session.close();
        }
        if (null == result || result.size() < 1) {
            return null;
        }
        return result.get(0);
    }

    @SuppressWarnings("unchecked")
    public List<Document> retriveByTime(String time, int begin, int limit) {
        List<Document> result = new ArrayList<Document>();
        Session session = getHibernateTemplate().getSessionFactory().openSession();
        try {
            Query query = session
                    .createQuery("from CommonDocument where downloadTime>:time order by downloadTime desc");
            query.setFirstResult(begin);
            if (limit > 0) {
                query.setMaxResults(limit);
            }
            query.setParameter("time", time);
            result = query.list();
            session.flush();
        } catch (Exception ex) {
            LOG.warn(ex.getMessage());
        } finally {
            session.close();
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public List<Document> retriveAll(int begin, int limit) {
        List<Document> result = new ArrayList<Document>();
        Session session = getHibernateTemplate().getSessionFactory().openSession();
        try {
            Query query = session.createQuery("from CommonDocument order by downloadTime desc");
            query.setFirstResult(begin);
            if (limit > 0) {
                query.setMaxResults(limit);
            }
            result = query.list();
            session.flush();
        } catch (Exception ex) {
            LOG.warn(ex.getMessage());
        } finally {
            session.close();
        }
        return result;
    }
}
