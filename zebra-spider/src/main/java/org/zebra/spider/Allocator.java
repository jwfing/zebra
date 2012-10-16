package org.zebra.spider;

import java.util.List;
import org.zebra.common.domain.Seed;

public interface Allocator {
	public boolean initialize();
	public void destory();
    public List<Seed> getUrls(int max);
    public void setCollection(SeedCollection collection);
}
