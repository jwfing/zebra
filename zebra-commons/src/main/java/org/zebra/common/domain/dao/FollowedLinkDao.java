package org.zebra.common.domain.dao;

import java.io.Serializable;
import java.util.*;
import org.zebra.common.domain.FollowedLink;

public interface FollowedLinkDao {
    public void delete(FollowedLink link);
    public Serializable save(FollowedLink link);
    public List<FollowedLink> getLinks(long beginTimeCreated, long endTimeCreated, int offset, int limit);
}
