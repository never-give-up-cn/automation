package com.never_give_up.automation.demo.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class DnsPacket extends BasePacket {
    private int transactionId;
    private boolean recursionDesired;
    private boolean recursionAvailable;
    private int questionCount;
    private int answerCount;
    private int authorityCount;
    private int additionalCount;
    private String domain;
    private int queryType;
    private String resolvedIp;
    private long ttl;
    private List<DnsRecord> records = new ArrayList<>();

    @Data
    public static class DnsRecord {
        private String name;
        private int type;
        private int clazz;
        private long ttl;
        private String data;
    }

    public boolean isQuery() { return questionCount > 0 && answerCount == 0; }
    public boolean isResponse() { return answerCount > 0; }

    @Override
    public byte[] serialize() {
        byte[] header = new byte[12];

        header[0] = (byte) (transactionId >> 8);
        header[1] = (byte) transactionId;

        int flags = 0;
        if (recursionDesired) flags |= 0x0100;
        if (recursionAvailable) flags |= 0x8000;
        header[2] = (byte) (flags >> 8);
        header[3] = (byte) flags;

        header[4] = (byte) (questionCount >> 8);
        header[5] = (byte) questionCount;
        header[6] = (byte) (answerCount >> 8);
        header[7] = (byte) answerCount;
        header[8] = (byte) (authorityCount >> 8);
        header[9] = (byte) authorityCount;
        header[10] = (byte) (additionalCount >> 8);
        header[11] = (byte) additionalCount;

        byte[] question = encodeDomainName(domain);
        byte[] questionType = new byte[4];
        questionType[0] = (byte) (queryType >> 8);
        questionType[1] = (byte) queryType;
        questionType[2] = 0;
        questionType[3] = 1;

        byte[] result = new byte[header.length + question.length + questionType.length];
        System.arraycopy(header, 0, result, 0, header.length);
        System.arraycopy(question, 0, result, header.length, question.length);
        System.arraycopy(questionType, 0, result, header.length + question.length, questionType.length);

        return result;
    }

    @Override
    public void deserialize(byte[] data) {
        if (data.length < 12) return;

        transactionId = ((data[0] & 0xFF) << 8) | (data[1] & 0xFF);

        int flags = ((data[2] & 0xFF) << 8) | (data[3] & 0xFF);
        recursionDesired = (flags & 0x0100) != 0;
        recursionAvailable = (flags & 0x8000) != 0;

        questionCount = ((data[4] & 0xFF) << 8) | (data[5] & 0xFF);
        answerCount = ((data[6] & 0xFF) << 8) | (data[7] & 0xFF);
        authorityCount = ((data[8] & 0xFF) << 8) | (data[9] & 0xFF);
        additionalCount = ((data[10] & 0xFF) << 8) | (data[11] & 0xFF);
    }

    private byte[] encodeDomainName(String domain) {
        String[] labels = domain.split("\\.");
        int totalLength = 0;
        for (String label : labels) {
            totalLength += 1 + label.length();
        }
        totalLength += 1;

        byte[] encoded = new byte[totalLength];
        int pos = 0;

        for (String label : labels) {
            encoded[pos++] = (byte) label.length();
            for (byte b : label.getBytes()) {
                encoded[pos++] = b;
            }
        }
        encoded[pos] = 0;

        return encoded;
    }
}
