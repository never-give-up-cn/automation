package com.never_give_up.automation.demo.factory.vlan;
//15. VLAN 802.1Q 工厂（VlanFactory）
public class VlanFactory {
    public byte[] addVlan(byte[] frame, int vid) {
        byte[] tagged = new byte[frame.length + 4];
        System.arraycopy(frame, 0, tagged, 0, 12);
        tagged[12] = (byte) 0x81;
        tagged[13] = (byte) 0x00;
        tagged[14] = (byte) ((vid >> 8) & 0x0F);
        tagged[15] = (byte) vid;
        System.arraycopy(frame, 12, tagged, 16, frame.length - 12);
        return tagged;
    }
}