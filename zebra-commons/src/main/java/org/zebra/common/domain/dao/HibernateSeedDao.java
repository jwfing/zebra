package org.zebra.common.domain.dao;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.zebra.common.domain.Seed;

public class HibernateSeedDao extends HibernateDaoSupport implements SeedDao {
    protected Logger logger = LoggerFactory.getLogger(getClass().getName());

    @Override
    public Serializable save(Seed seed) {
        return getHibernateTemplate().save(seed);
    }

    @Override
    public void delete(Seed seed) {
        getHibernateTemplate().delete(seed);
    }

    @Override
    public void update(Seed seed) {
        getHibernateTemplate().merge(seed);
    }

    @Override
    public List<Seed> getSeeds(long fetchTime, int offset, int limit) {
        List<Seed> result = null;
        Session session = getHibernateTemplate().getSessionFactory().openSession();
        try {
            Query query = session.createQuery("from Seed where next_fetch<=:fetchTime");
            query.setParameter("fetchTime", fetchTime);
            query.setFirstResult(offset);
            if (limit > 0) {
                query.setMaxResults(limit);
            }
            result = query.list();
            session.flush();
        } catch (Exception ex) {
            logger.warn(ex.getMessage());
        } finally {
            session.close();
        }
        return result;
    }

    @Override
    public void cleanAll() {
        getHibernateTemplate().clear();
    }

    @Override
    public Seed loadByUrlmd5(String urlmd5) {
        List<Seed> result = null;
        Session session = getHibernateTemplate().getSessionFactory().openSession();
        try {
            Query query = session.createQuery("from Seed where urlMd5=:urlMd5");
            query.setParameter("urlMd5", urlmd5);
            result = query.list();
            session.flush();
        } catch (Exception ex) {
            logger.warn(ex.getMessage());
        } finally {
            session.close();
        }
        if (null == result || result.size() < 1) {
            return null;
        }
        return result.get(0);
    }
}
