package org.onelab.filter;

import junit.framework.TestCase;

public class BloomFilterTest extends TestCase {
    public void testBloomFilter() {
        BloomFilter filter = new BloomFilter(1250000, 10);
    }

}
