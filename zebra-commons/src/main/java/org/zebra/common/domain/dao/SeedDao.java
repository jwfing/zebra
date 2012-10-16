package org.zebra.common.domain.dao;

import org.zebra.common.domain.Seed;
import java.util.*;
import java.io.Serializable;

public interface SeedDao {
    public Serializable save(Seed seed);
    public void delete(Seed seed);
    public void update(Seed seed);
    public void cleanAll();
    public Seed loadByUrlmd5(String urlmd5);
    public List<Seed> getSeeds(long fetchTime, int offset, int limit);
}
