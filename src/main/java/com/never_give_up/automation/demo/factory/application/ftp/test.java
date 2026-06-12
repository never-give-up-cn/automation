package com.never_give_up.automation.demo.factory.application.ftp;

import java.util.List;

public class test {
    public static void main(String[] args) {
        FtpResponseParser parser = new FtpResponseParser();

// 1. 解析单行响应
        String response = "220 Service ready for new user";
        FtpResponseParser.FtpResponse resp = parser.parse(response);
        System.out.println("Code: " + resp.getCode());        // 220
        System.out.println("Message: " + resp.getMessage());  // "Service ready for new user"
        System.out.println("Is success: " + resp.isSuccess()); // true

// 2. 解析多行响应
        String multiResponse = "211-Features:\n" +
                " MLSD\n" +
                " REST STREAM\n" +
                "211 End";
        FtpResponseParser.FtpResponse multiResp = parser.parseMultiLine(multiResponse);
        System.out.println("Code: " + multiResp.getCode());          // 211
        System.out.println("Multi-line: " + multiResp.isMultiLine()); // true
        System.out.println("Lines count: " + multiResp.getMultiLineMessages().size()); // 3

// 3. 批量解析
        byte[] data = "220 Service ready\r\n331 User name okay, need password\r\n".getBytes();
        List<FtpResponseParser.FtpResponse> responses = parser.parseAll(data);
        for (FtpResponseParser.FtpResponse r : responses) {
            System.out.println(r.getCode() + ": " + r.getMessage());
        }

// 4. 解析PASV响应中的IP和端口
        String pasvResp = "227 Entering Passive Mode (192,168,1,100,234,150)";
        FtpResponseParser.FtpResponse pasv = parser.parse(pasvResp);
        String[] ipPort = parser.extractPassiveInfo(pasv);
        System.out.println("IP: " + ipPort[0]);      // 192.168.1.100
        System.out.println("Port: " + ipPort[1]);    // 60054

// 5. 验证响应格式
        boolean valid = parser.isValidResponse("220 Hello".getBytes()); // true
        boolean invalid = parser.isValidResponse("Invalid".getBytes()); // false

// 6. 获取响应码描述
        String desc = FtpResponseParser.getResponseDescription(227);
        System.out.println(desc); // "Entering Passive Mode"
    }
}
