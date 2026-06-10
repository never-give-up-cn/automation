package com.never_give_up.automation.demo.factory.security;

import java.util.HashMap;
import java.util.Map;

/** DDoS 缓解/防护 */
public class DdosMitigationFactory {
    private final Map<String, Integer> ipCounter = new HashMap<>();
    private final int threshold = 100;

    public boolean checkTraffic(String srcIp) {
        int cnt = ipCounter.getOrDefault(srcIp, 0) + 1;
        ipCounter.put(srcIp, cnt);
        return cnt < threshold;
    }

    public void reset() {
        ipCounter.clear();
    }
}