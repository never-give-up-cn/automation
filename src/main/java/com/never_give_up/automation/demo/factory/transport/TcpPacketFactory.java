package com.never_give_up.automation.demo.factory.transport;

import com.never_give_up.automation.demo.core.INetworkFactory;
import com.never_give_up.automation.demo.model.TcpPacket;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TcpPacketFactory implements INetworkFactory<TcpPacket> {
    private int sequenceNumber = 100;
    private int acknowledgmentNumber = 0;
    private int windowSize = 3;
    private int ssthresh = 12;
    private int cwnd = 1;

    public TcpPacket createSyn() {
        TcpPacket packet = new TcpPacket();
        packet.setPacketType("SYN");
        packet.setSequenceNumber(sequenceNumber++);
        packet.setFlags("SYN");
        packet.setWindowSize(windowSize);
        return packet;
    }

    public TcpPacket createSynAck(int ackSeq) {
        TcpPacket packet = new TcpPacket();
        packet.setPacketType("SYN_ACK");
        packet.setSequenceNumber(200);
        packet.setAcknowledgmentNumber(ackSeq + 1);
        packet.setFlags("SYN,ACK");
        packet.setWindowSize(windowSize);
        return packet;
    }

    public TcpPacket createDataPacket(int seq, byte[] payload) {
        TcpPacket packet = new TcpPacket();
        packet.setPacketType("DATA");
        packet.setSequenceNumber(seq);
        packet.setPayload(payload);
        packet.setFlags("PSH,ACK");
        packet.setWindowSize(windowSize);
        return packet;
    }

    public TcpPacket createAck(int ackSeq) {
        TcpPacket packet = new TcpPacket();
        packet.setPacketType("ACK");
        packet.setAcknowledgmentNumber(ackSeq + 1);
        packet.setFlags("ACK");
        return packet;
    }

    public TcpPacket createFin() {
        TcpPacket packet = new TcpPacket();
        packet.setPacketType("FIN");
        packet.setFlags("FIN,ACK");
        return packet;
    }

    @Override
    public TcpPacket produce() {
        return createDataPacket(sequenceNumber++, new byte[0]);
    }

    @Override
    public void reset() {
        sequenceNumber = 100;
        acknowledgmentNumber = 0;
        cwnd = 1;
        ssthresh = 12;
    }
}
