package org.zebra.silkworm.plugin.dbstorage;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class HibernateNewsDao extends HibernateDaoSupport  implements NewsDao {
	private static final Log LOG = LogFactory.getLog(HibernateNewsDao.class);

	public Serializable save(News news) {
		return getHibernateTemplate().save(news);
	}
	public void update(News news) {
		getHibernateTemplate().merge(news);
	}
	public void delete(News news) {
		getHibernateTemplate().delete(news);
	}
	@SuppressWarnings("unchecked")
	public News loadByUrl(String url) {
		List<News> result = new ArrayList<News>();
		Session session = getHibernateTemplate().getSessionFactory().openSession();
        try {
            Query query = session.createQuery("from News where url=:url");
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
	public List<News> retriveByTime(String time, int begin, int limit) {
		List<News> result = new ArrayList<News>();
		Session session = getHibernateTemplate().getSessionFactory().openSession();
        try {
            Query query = session.createQuery("from News where downloadTime>:time order by downloadTime desc");
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
	public List<News> retriveAll(int begin, int limit) {
		List<News> result = new ArrayList<News>();
		Session session = getHibernateTemplate().getSessionFactory().openSession();
        try {
            Query query = session.createQuery("from News order by downloadTime desc");
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
