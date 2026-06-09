package com.never_give_up.automation.demo.factory.network;

import com.never_give_up.automation.demo.model.IpPacket;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class IpFragmentFactory {
    private static final int MAX_FRAGMENT_SIZE = 576;
    private final Map<FragmentKey, List<IpFragment>> fragmentBuffer = new HashMap<>();
    private int identifierCounter = 2000;

    public record FragmentKey(int identification, String srcIp, String dstIp, int protocol) {}

    public List<IpPacket> fragmentPacket(IpPacket packet) {
        byte[] payload = packet.getPayload();
        if (payload == null || payload.length <= MAX_FRAGMENT_SIZE) {
            return List.of(packet);
        }

        List<IpPacket> fragments = new ArrayList<>();
        int offset = 0;
        int fragmentId = identifierCounter++;

        while (offset < payload.length) {
            int fragmentSize = Math.min(MAX_FRAGMENT_SIZE, payload.length - offset);
            byte[] fragmentData = new byte[fragmentSize];
            System.arraycopy(payload, offset, fragmentData, 0, fragmentSize);

            IpPacket fragment = new IpPacket();
            fragment.setPacketType("IP_FRAGMENT");
            fragment.setSourceIp(packet.getSourceIp());
            fragment.setDestinationIp(packet.getDestinationIp());
            fragment.setProtocol(packet.getProtocol());
            fragment.setTotalLength(fragmentData.length);
            fragment.setIdentification(fragmentId);
            fragment.setTtl(packet.getTtl());
            fragment.setFragmentOffset(offset / 8);
            fragment.setFlags(offset + fragmentSize < payload.length ? 1 : 0);
            fragment.setPayload(fragmentData);

            fragments.add(fragment);
            offset += fragmentSize;
        }

        return fragments;
    }

    public IpPacket reassembleFragments(FragmentKey key, IpPacket fragment) {
        List<IpFragment> fragments = fragmentBuffer.computeIfAbsent(key, k -> new ArrayList<>());

        fragments.add(new IpFragment(
                fragment.getFragmentOffset() * 8,
                fragment.getFlags() == 1,
                fragment.getPayload()
        ));

        boolean allReceived = fragments.stream().noneMatch(IpFragment::isMoreFragments);
        if (!allReceived) {
            return null;
        }

        fragments.sort((a, b) -> Integer.compare(a.getOffset(), b.getOffset()));

        int totalLength = fragments.stream().mapToInt(f -> f.getData().length).sum();
        byte[] reassembled = new byte[totalLength];
        int offset = 0;

        for (IpFragment frag : fragments) {
            System.arraycopy(frag.getData(), 0, reassembled, offset, frag.getData().length);
            offset += frag.getData().length;
        }

        IpPacket reassembledPacket = new IpPacket();
        reassembledPacket.setPacketType("IP");
        reassembledPacket.setSourceIp(fragment.getSourceIp());
        reassembledPacket.setDestinationIp(fragment.getDestinationIp());
        reassembledPacket.setProtocol(fragment.getProtocol());
        reassembledPacket.setTotalLength(totalLength);
        reassembledPacket.setIdentification(fragment.getIdentification());
        reassembledPacket.setTtl(fragment.getTtl());
        reassembledPacket.setPayload(reassembled);

        fragmentBuffer.remove(key);

        return reassembledPacket;
    }

    private record IpFragment(int offset, boolean moreFragments, byte[] data) {}

    public void clearBuffer() {
        fragmentBuffer.clear();
    }

    public void reset() {
        fragmentBuffer.clear();
        identifierCounter = 2000;
    }
}
