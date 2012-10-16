package org.zebra.silkworm.plugin.clustering;

import java.util.*;
import java.util.Map.Entry;

import org.zebra.search.crawler.util.StringUtil;

public class SimHashDigister {
    private final static long[] BITS = { 0x0001l << 0, 0x0001l << 1, 0x0001l << 2, 0x0001l << 3,
            0x0001l << 4, 0x0001l << 5, 0x0001l << 6, 0x0001l << 7, 0x0001l << 8, 0x0001l << 9,
            0x0001l << 10, 0x0001l << 11, 0x0001l << 12, 0x0001l << 13, 0x0001l << 14,
            0x0001l << 15, 0x0001l << 16, 0x0001l << 17, 0x0001l << 18, 0x0001l << 19,
            0x0001l << 20, 0x0001l << 21, 0x0001l << 22, 0x0001l << 23, 0x0001l << 24,
            0x0001l << 25, 0x0001l << 26, 0x0001l << 27, 0x0001l << 28, 0x0001l << 29,
            0x0001l << 30, 0x0001l << 31, 0x0001l << 32, 0x0001l << 33, 0x0001l << 34,
            0x0001l << 35, 0x0001l << 36, 0x0001l << 37, 0x0001l << 38, 0x0001l << 39,
            0x0001l << 40, 0x0001l << 41, 0x0001l << 42, 0x0001l << 43, 0x0001l << 44,
            0x0001l << 45, 0x0001l << 46, 0x0001l << 47, 0x0001l << 48, 0x0001l << 49,
            0x0001l << 50, 0x0001l << 51, 0x0001l << 52, 0x0001l << 53, 0x0001l << 54,
            0x0001l << 55, 0x0001l << 56, 0x0001l << 57, 0x0001l << 58, 0x0001l << 59,
            0x0001l << 60, 0x0001l << 61, 0x0001l << 62, 0x0001l << 63, };

    public long digister(String[] tokens) {
        long result = 0l;
        Map<Long, Integer> termPosMap = new HashMap<Long, Integer>();
        List<Integer> termCountList = new ArrayList<Integer>();
        int currentIndex = 0;
        for (String token : tokens) {
            long hashValue = StringUtil.FNVHash(token);
            if (termPosMap.containsKey(hashValue)) {
                Integer index = termPosMap.get(hashValue);
                Integer counter = termCountList.get(index);
                counter++;
                termCountList.set(index, counter);
            } else {
                termCountList.add(1);
                termPosMap.put(hashValue, currentIndex);
                currentIndex++;
            }
        }
        Set<Entry<Long, Integer>> entries = termPosMap.entrySet();
        int loopLimit = BITS.length;
        for (int i = 0; i < loopLimit; ++i) {
            long currentBitResult = 0l;
            for (Entry<Long, Integer> entry : entries) {
                Long hashValue = entry.getKey();
                Integer index = entry.getValue();
                if (index < 0l || index >= termCountList.size()) {
                    continue;
                }
                Integer counter = termCountList.get(index);
                if ((hashValue & BITS[i]) == BITS[i]) {
                    // bitValue == 1
                    currentBitResult += counter;
                } else {
                    // bitValue == 0
                    currentBitResult -= counter;
                }
            }
            if (currentBitResult > 0) {
                currentBitResult = 1;
            } else {
                currentBitResult = 0;
            }
            result |= (currentBitResult << i);
        }

        return result;
    }
}
