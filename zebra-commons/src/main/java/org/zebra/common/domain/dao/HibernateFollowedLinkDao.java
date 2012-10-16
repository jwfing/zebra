package org.zebra.common.domain.dao;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.zebra.common.domain.FollowedLink;
import org.zebra.common.domain.Seed;

public class HibernateFollowedLinkDao extends HibernateDaoSupport implements FollowedLinkDao {
    private static final Log LOG = LogFactory.getLog(HibernateFollowedLinkDao.class);

    @Override
    public void delete(FollowedLink link) {
        getHibernateTemplate().delete(link);
    }

    @Override
    public Serializable save(FollowedLink link) {
        return getHibernateTemplate().save(link);
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
            LOG.warn(ex.getMessage());
        } finally {
            session.close();
        }
        return result;
    }
}
