package org.zebra.silkworm.plugin.dbstorage;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class HibernateDocumentDao extends HibernateDaoSupport implements DocumentDao {
    private static final Log LOG = LogFactory.getLog(HibernateDocumentDao.class);

    public Serializable save(CommonDocument doc) {
        return getHibernateTemplate().save(doc);
    }

    public void update(CommonDocument doc) {
        getHibernateTemplate().merge(doc);
    }

    public void delete(CommonDocument doc) {
        getHibernateTemplate().delete(doc);
    }

    @SuppressWarnings("unchecked")
    public CommonDocument loadByUrl(String url) {
        List<CommonDocument> result = new ArrayList<CommonDocument>();
        Session session = getHibernateTemplate().getSessionFactory().openSession();
        try {
            Query query = session.createQuery("from CommonDocument where url=:url");
            query.setParameter("url", url);
            result = query.list();
            session.flush();
        } catch (Exception ex) {
            if (LOG.isWarnEnabled()) {
                LOG.warn(ex.getMessage());
            }
        } finally {
            session.close();
        }
        if (null == result || result.size() < 1) {
            return null;
        }
        return result.get(0);
    }

    @SuppressWarnings("unchecked")
    public List<CommonDocument> retriveByTime(String time, int begin, int limit) {
        List<CommonDocument> result = new ArrayList<CommonDocument>();
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
            if (LOG.isWarnEnabled()) {
                LOG.warn(ex.getMessage());
            }
        } finally {
            session.close();
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public List<CommonDocument> retriveAll(int begin, int limit) {
        List<CommonDocument> result = new ArrayList<CommonDocument>();
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
            if (LOG.isWarnEnabled()) {
                LOG.warn(ex.getMessage());
            }
        } finally {
            session.close();
        }
        return result;
    }
}
