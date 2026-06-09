package com.never_give_up.automation.demo.factory.application;

import com.never_give_up.automation.demo.core.INetworkFactory;
import com.never_give_up.automation.demo.model.DnsPacket;

import java.util.Random;

public class DnsPacketFactory implements INetworkFactory<DnsPacket> {
    private final Random random = new Random();

    public DnsPacket createQuery(String domain) {
        DnsPacket packet = new DnsPacket();
        packet.setPacketType("DNS_QUERY");
        packet.setTransactionId(random.nextInt(65536));
        packet.setRecursionDesired(true);
        packet.setQuestionCount(1);
        packet.setAnswerCount(0);
        packet.setDomain(domain);
        packet.setQueryType(1);
        return packet;
    }

    public DnsPacket createResponse(String domain, String resolvedIp, int transactionId) {
        DnsPacket packet = new DnsPacket();
        packet.setPacketType("DNS_RESPONSE");
        packet.setTransactionId(transactionId);
        packet.setRecursionDesired(true);
        packet.setRecursionAvailable(true);
        packet.setQuestionCount(1);
        packet.setAnswerCount(1);
        packet.setDomain(domain);
        packet.setResolvedIp(resolvedIp);
        packet.setTtl(3600);

        DnsPacket.DnsRecord record = new DnsPacket.DnsRecord();
        record.setName(domain);
        record.setType(1);
        record.setClazz(1);
        record.setTtl(3600);
        record.setData(resolvedIp);
        packet.getRecords().add(record);

        return packet;
    }

    public DnsPacket createRecursiveQuery(String domain, int queryType) {
        DnsPacket packet = createQuery(domain);
        packet.setQueryType(queryType);
        packet.setPacketType("DNS_RECURSIVE_QUERY");
        return packet;
    }

    @Override
    public DnsPacket produce() {
        return createQuery("example.com");
    }

    @Override
    public void reset() {
    }
}
