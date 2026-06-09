package com.never_give_up.automation.demo.factory.link;

import com.never_give_up.automation.demo.core.INetworkFactory;
import com.never_give_up.automation.demo.model.EthernetFrame;

public class EthernetFactory implements INetworkFactory<EthernetFrame> {
    public EthernetFrame createFrame(String srcMac, String dstMac, int etherType) {
        EthernetFrame frame = new EthernetFrame();
        frame.setPacketType("ETHERNET");
        frame.setSourceMac(srcMac);
        frame.setDestinationMac(dstMac);
        frame.setEtherType(etherType);
        return frame;
    }

    public EthernetFrame createIpFrame(String srcMac, String dstMac) {
        return createFrame(srcMac, dstMac, 0x0800);
    }

    public EthernetFrame createArpFrame(String srcMac, String dstMac) {
        return createFrame(srcMac, dstMac, 0x0806);
    }

    @Override
    public EthernetFrame produce() {
        return createIpFrame("00:00:00:00:00:00", "FF:FF:FF:FF:FF:FF");
    }

    @Override
    public void reset() {
    }
}
