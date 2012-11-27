package org.zebra.common.domain.dao;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.zebra.common.domain.FollowedLink;
import org.zebra.common.domain.Seed;

public class HibernateFollowedLinkDao extends HibernateDaoSupport implements FollowedLinkDao {
    protected Logger logger = LoggerFactory.getLogger(getClass().getName());

    @Override
    public void delete(FollowedLink link) {
        getHibernateTemplate().delete(link);
    }

    @Override
    public Serializable save(FollowedLink link) {
        return getHibernateTemplate().save(link);
    }

    @Override
    public void update(FollowedLink link) {
        getHibernateTemplate().merge(link);
    }

    @Override
    public List<FollowedLink> getLinks(long beginTimeCreated,
            long endTimeCreated, int offset, int limit) {
        List<FollowedLink> result = null;
        Session session = getHibernateTemplate().getSessionFactory().openSession();
        try {
            Query query = session.createQuery("from FollowedLink where timeCreated >= :beginTime and timeCreated<:endTime");
            query.setParameter("beginTime", beginTimeCreated);
            query.setParameter("endTime", endTimeCreated);
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
}
