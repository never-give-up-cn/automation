package com.never_give_up.automation.demo.factory.transport;

import com.never_give_up.automation.demo.core.INetworkFactory;
import com.never_give_up.automation.demo.model.TcpPacket;

public class TcpPacketFactory implements INetworkFactory<TcpPacket> {
    private int sequenceNumber = 100;
    private int acknowledgmentNumber = 0;
    private int windowSize = 3;
    private int ssthresh = 12;
    private int cwnd = 1;
    private int packetsAckedSinceLastIncrease = 0;

    public TcpPacket createSyn(int destPort) {
        TcpPacket packet = new TcpPacket();
        packet.setPacketType("SYN");
        packet.setDestinationPort(destPort);
        packet.setSequenceNumber(sequenceNumber++);
        packet.setFlags("SYN");
        packet.setWindowSize(windowSize);
        return packet;
    }

    public TcpPacket createSynAck(int ackSeq, int destPort) {
        TcpPacket packet = new TcpPacket();
        packet.setPacketType("SYN_ACK");
        packet.setDestinationPort(destPort);
        packet.setSequenceNumber(200);
        packet.setAcknowledgmentNumber(ackSeq + 1);
        packet.setFlags("SYN,ACK");
        packet.setWindowSize(windowSize);
        return packet;
    }

    public TcpPacket createDataPacket(int seq, byte[] payload, int destPort) {
        TcpPacket packet = new TcpPacket();
        packet.setPacketType("DATA");
        packet.setDestinationPort(destPort);
        packet.setSequenceNumber(seq);
        packet.setPayload(payload);
        packet.setFlags("PSH,ACK");
        packet.setWindowSize(windowSize);
        return packet;
    }

    public TcpPacket createAck(int ackSeq, int destPort) {
        TcpPacket packet = new TcpPacket();
        packet.setPacketType("ACK");
        packet.setDestinationPort(destPort);
        packet.setAcknowledgmentNumber(ackSeq + 1);
        packet.setFlags("ACK");
        return packet;
    }

    public TcpPacket createFin(int destPort) {
        TcpPacket packet = new TcpPacket();
        packet.setPacketType("FIN");
        packet.setDestinationPort(destPort);
        packet.setFlags("FIN,ACK");
        return packet;
    }

    public TcpPacket createRetransmit(int seq, byte[] payload, int destPort) {
        TcpPacket packet = createDataPacket(seq, payload, destPort);
        packet.setRetransmission(true);
        return packet;
    }

    public void slowStart() {
        if (cwnd < ssthresh) {
            cwnd++;
        } else {
            packetsAckedSinceLastIncrease++;
            if (packetsAckedSinceLastIncrease >= cwnd) {
                cwnd++;
                packetsAckedSinceLastIncrease = 0;
            }
        }
    }

    public void congestionAvoidance() {
        ssthresh = Math.max(2, cwnd / 2);
        cwnd = 1;
        packetsAckedSinceLastIncrease = 0;
    }

    public void setWindowSize(int size) {
        this.windowSize = size;
    }

    public int getCwnd() { return cwnd; }
    public int getSsthresh() { return ssthresh; }
    public int getNextSeq() { return sequenceNumber; }

    @Override
    public TcpPacket produce() {
        return createDataPacket(sequenceNumber++, new byte[0], 80);
    }

    @Override
    public void reset() {
        sequenceNumber = 100;
        acknowledgmentNumber = 0;
        cwnd = 1;
        ssthresh = 12;
        packetsAckedSinceLastIncrease = 0;
    }
}
