package com.never_give_up.automation.demo.factory.network.ipv6;

import java.util.ArrayList;
import java.util.List;

public class Ipv6FragmentFactory {
    private static final int IPV6_MTU = 1280;
    private int fragmentId = 0;

    public List<byte[]> fragmentPacket(byte[] originalPacket) {
        List<byte[]> fragments = new ArrayList<>();
        int headerLen = 40;
        int maxPayload = IPV6_MTU - headerLen - 8;
        int payloadLen = originalPacket.length - headerLen;
        int offset = 0;

        while (offset < payloadLen) {
            int len = Math.min(maxPayload, payloadLen - offset);
            byte[] fragment = new byte[headerLen + 8 + len];
            System.arraycopy(originalPacket, 0, fragment, 0, headerLen);

            fragment[6] = (byte) 44;
            fragment[40] = (byte) ((fragmentId >> 24) & 0xFF);
            fragment[41] = (byte) ((fragmentId >> 16) & 0xFF);
            fragment[42] = (byte) ((fragmentId >> 8) & 0xFF);
            fragment[43] = (byte) (fragmentId & 0xFF);
            fragment[44] = (byte) ((offset / 8) >> 8);
            fragment[45] = (byte) (offset / 8);

            // 修复：强转 byte
            fragment[46] = (byte) ((offset + len >= payloadLen) ? 0 : 1);

            System.arraycopy(originalPacket, headerLen + offset, fragment, headerLen + 8, len);
            fragments.add(fragment);
            offset += len;
        }
        fragmentId++;
        return fragments;
    }

    public void reset() {
        fragmentId = 0;
    }
}