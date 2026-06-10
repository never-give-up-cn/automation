package com.never_give_up.automation.demo.factory.nat;

import java.util.HashMap;
import java.util.Map;

/** NAT 发夹转换（内网主机通过公网地址互访） */
public class NatHairpinningFactory {
    private final Map<String, String> internalMap = new HashMap<>();

    public String hairpinTranslate(String dstPubIp, String srcIntIp) {
        // 公网地址转回内网地址
        return internalMap.getOrDefault(dstPubIp, dstPubIp);
    }

    public void addMapping(String pubIp, String intIp) {
        internalMap.put(pubIp, intIp);
    }

    public void reset() {
        internalMap.clear();
    }
}