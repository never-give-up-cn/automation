package com.never_give_up.automation.demo.factory.application.ftp;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * FTP响应解析器 - 解析服务器响应
 * 符合RFC 959标准
 */
public class FtpResponseParser {

    /**
     * FTP响应类
     */
    public static class FtpResponse {
        private int code;                    // 响应码: 220, 331, 230, 150, 226等
        private String message;              // 响应消息
        private FtpResponseType type;        // 响应类型
        private String fullLine;             // 原始响应行
        private boolean isMultiLine;         // 是否是多行响应
        private List<String> multiLineMessages; // 多行响应的所有行

        public enum FtpResponseType {
            POSITIVE_COMPLETION,    // 2xx 成功完成
            POSITIVE_INTERMEDIATE,  // 3xx 需要更多信息
            TRANSIENT_NEGATIVE,     // 4xx 临时错误，可重试
            PERMANENT_NEGATIVE      // 5xx 永久错误，不可恢复
        }

        // Getters
        public int getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }

        public FtpResponseType getType() {
            return type;
        }

        public String getFullLine() {
            return fullLine;
        }

        public boolean isMultiLine() {
            return isMultiLine;
        }

        public List<String> getMultiLineMessages() {
            return multiLineMessages;
        }

        // 辅助方法
        public boolean isSuccess() {
            return code >= 200 && code < 300;
        }

        public boolean isNeedMoreInfo() {
            return code >= 300 && code < 400;
        }

        public boolean isTempError() {
            return code >= 400 && code < 500;
        }

        public boolean isPermError() {
            return code >= 500 && code < 600;
        }

        public boolean isLoginSuccess() {
            return code == 230;  // User logged in
        }

        public boolean isNeedPassword() {
            return code == 331;  // User name okay, need password
        }

        public boolean isPassiveMode() {
            return code == 227;  // Entering Passive Mode
        }

        public boolean isDataConnOpening() {
            return code == 150;  // File status okay; about to open data connection
        }

        public boolean isTransferComplete() {
            return code == 226;  // Closing data connection
        }

        @Override
        public String toString() {
            return String.format("%d %s", code, message);
        }
    }

    /**
     * 解析单行FTP响应
     * 格式: <code> <message>
     * 例如: "220 Service ready"
     *
     * @param data 响应数据字节数组
     * @return FtpResponse对象，解析失败返回null
     */
    public FtpResponse parse(byte[] data) {
        if (data == null || data.length == 0) {
            return null;
        }

        String response = new String(data, StandardCharsets.ISO_8859_1);
        return parse(response);
    }

    /**
     * 解析单行FTP响应字符串
     *
     * @param responseLine 响应字符串
     * @return FtpResponse对象，解析失败返回null
     */
    public FtpResponse parse(String responseLine) {
        if (responseLine == null || responseLine.trim().isEmpty()) {
            return null;
        }

        // 去除末尾的换行符
        responseLine = responseLine.trim();

        try {
            FtpResponse resp = new FtpResponse();
            resp.fullLine = responseLine;
            resp.isMultiLine = false;

            // 正则表达式匹配: 3位数字 + 空格/连字符 + 消息内容
            Pattern pattern = Pattern.compile("^(\\d{3})([- ])(.*)$");
            Matcher matcher = pattern.matcher(responseLine);

            if (!matcher.find()) {
                return null;
            }

            // 解析响应码
            resp.code = Integer.parseInt(matcher.group(1));

            // 判断是多行响应的开始（连字符表示多行）
            String separator = matcher.group(2);
            resp.isMultiLine = "-".equals(separator);

            // 解析消息内容
            resp.message = matcher.group(3).trim();

            // 确定响应类型
            resp.type = determineResponseType(resp.code);

            return resp;

        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 解析多行FTP响应
     * 多行响应格式:
     * 211-Features:
     * MLSD
     * REST STREAM
     * 211 End
     *
     * @param data 多行响应数据
     * @return 包含所有行的FtpResponse对象
     */
    public FtpResponse parseMultiLine(byte[] data) {
        if (data == null || data.length == 0) {
            return null;
        }

        String responseStr = new String(data, StandardCharsets.ISO_8859_1);
        return parseMultiLine(responseStr);
    }

    /**
     * 解析多行FTP响应字符串
     *
     * @param responseStr 多行响应字符串
     * @return 包含所有行的FtpResponse对象
     */
    public FtpResponse parseMultiLine(String responseStr) {
        if (responseStr == null || responseStr.trim().isEmpty()) {
            return null;
        }

        String[] lines = responseStr.split("\\r?\\n");
        if (lines.length == 0) {
            return null;
        }

        // 解析第一行
        FtpResponse response = parse(lines[0]);
        if (response == null) {
            return null;
        }

        response.isMultiLine = true;
        response.multiLineMessages = new ArrayList<>();

        // 添加后续行（跳过第一行，因为已经解析过）
        for (int i = 1; i < lines.length; i++) {
            String line = lines[i].trim();
            if (!line.isEmpty()) {
                response.multiLineMessages.add(line);

                // 如果是最后一行，且包含结束标记
                if (line.startsWith(String.valueOf(response.code)) && line.contains(" ")) {
                    response.message = line.substring(4).trim();
                }
            }
        }

        return response;
    }

    /**
     * 批量解析响应（适用于一次读取多个响应）
     *
     * @param data 响应数据
     * @return 响应列表
     */
    public List<FtpResponse> parseAll(byte[] data) {
        List<FtpResponse> responses = new ArrayList<>();
        if (data == null || data.length == 0) {
            return responses;
        }

        String fullResponse = new String(data, StandardCharsets.ISO_8859_1);
        String[] lines = fullResponse.split("\\r?\\n");

        StringBuilder multiLineBuffer = null;

        for (String line : lines) {
            if (line.trim().isEmpty()) {
                continue;
            }

            // 检查是否是多行响应的开始（3位数字 + 连字符）
            if (line.matches("^\\d{3}-.*$")) {
                // 开始收集多行响应
                multiLineBuffer = new StringBuilder();
                multiLineBuffer.append(line).append("\n");
            } else if (multiLineBuffer != null) {
                // 收集多行响应的后续行
                multiLineBuffer.append(line).append("\n");

                // 检查是否是多行响应的结束（3位数字 + 空格）
                if (line.matches("^\\d{3} .*$")) {
                    // 多行响应结束，解析
                    FtpResponse resp = parseMultiLine(multiLineBuffer.toString());
                    if (resp != null) {
                        responses.add(resp);
                    }
                    multiLineBuffer = null;
                }
            } else {
                // 单行响应
                FtpResponse resp = parse(line);
                if (resp != null) {
                    responses.add(resp);
                }
            }
        }

        // 处理未结束的多行响应
        if (multiLineBuffer != null) {
            FtpResponse resp = parseMultiLine(multiLineBuffer.toString());
            if (resp != null) {
                responses.add(resp);
            }
        }

        return responses;
    }

    /**
     * 根据响应码确定响应类型
     */
    private FtpResponse.FtpResponseType determineResponseType(int code) {
        if (code >= 200 && code < 300) {
            return FtpResponse.FtpResponseType.POSITIVE_COMPLETION;
        } else if (code >= 300 && code < 400) {
            return FtpResponse.FtpResponseType.POSITIVE_INTERMEDIATE;
        } else if (code >= 400 && code < 500) {
            return FtpResponse.FtpResponseType.TRANSIENT_NEGATIVE;
        } else if (code >= 500 && code < 600) {
            return FtpResponse.FtpResponseType.PERMANENT_NEGATIVE;
        }
        return null;
    }

    /**
     * 检查响应是否为指定类型
     */
    public boolean isResponseCode(FtpResponse response, int expectedCode) {
        return response != null && response.code == expectedCode;
    }

    /**
     * 从PASV响应中提取IP和端口信息
     *
     * @param response PASV响应
     * @return 包含IP和端口的数组 [ip, port]
     */
    public String[] extractPassiveInfo(FtpResponse response) {
        if (response == null || !response.isPassiveMode()) {
            return null;
        }

        String message = response.getMessage();
        // 匹配括号内的内容: (192,168,1,100,234,150)
        Pattern pattern = Pattern.compile("\\((\\d+,){5}\\d+\\)");
        Matcher matcher = pattern.matcher(message);

        if (matcher.find()) {
            String numbers = matcher.group();
            numbers = numbers.substring(1, numbers.length() - 1); // 去掉括号
            String[] parts = numbers.split(",");

            if (parts.length >= 6) {
                String ip = String.join(".", parts[0], parts[1], parts[2], parts[3]);
                int p1 = Integer.parseInt(parts[4]);
                int p2 = Integer.parseInt(parts[5]);
                int port = p1 * 256 + p2;
                return new String[]{ip, String.valueOf(port)};
            }
        }

        return null;
    }

    /**
     * 验证响应格式是否有效
     */
    public boolean isValidResponse(byte[] data) {
        if (data == null || data.length < 4) {
            return false;
        }

        String response = new String(data, StandardCharsets.ISO_8859_1).trim();
        return response.matches("^\\d{3}[- ].*$");
    }

    /**
     * 获取响应码的文本描述
     */
    public static String getResponseDescription(int code) {
        return switch (code) {
            case 110 -> "Restart marker reply";
            case 120 -> "Service ready in nnn minutes";
            case 125 -> "Data connection already open; transfer starting";
            case 150 -> "File status okay; about to open data connection";
            case 200 -> "Command okay";
            case 202 -> "Command not implemented";
            case 211 -> "System status, or system help reply";
            case 212 -> "Directory status";
            case 213 -> "File status";
            case 214 -> "Help message";
            case 215 -> "NAME system type";
            case 220 -> "Service ready for new user";
            case 221 -> "Service closing control connection";
            case 225 -> "Data connection open; no transfer in progress";
            case 226 -> "Closing data connection";
            case 227 -> "Entering Passive Mode";
            case 228 -> "Entering Long Passive Mode";
            case 229 -> "Entering Extended Passive Mode";
            case 230 -> "User logged in";
            case 250 -> "Requested file action okay, completed";
            case 257 -> "PATHNAME created";
            case 331 -> "User name okay, need password";
            case 332 -> "Need account for login";
            case 350 -> "Requested file action pending further information";
            case 421 -> "Service not available, closing control connection";
            case 425 -> "Can't open data connection";
            case 426 -> "Connection closed; transfer aborted";
            case 450 -> "Requested file action not taken";
            case 451 -> "Requested action aborted: local error in processing";
            case 452 -> "Requested action not taken: insufficient storage space";
            case 500 -> "Syntax error, command unrecognized";
            case 501 -> "Syntax error in parameters or arguments";
            case 502 -> "Command not implemented";
            case 503 -> "Bad sequence of commands";
            case 504 -> "Command not implemented for that parameter";
            case 530 -> "Not logged in";
            case 532 -> "Need account for storing files";
            case 550 -> "Requested action not taken";
            case 551 -> "Requested action aborted: page type unknown";
            case 552 -> "Requested file action aborted: exceeded storage allocation";
            case 553 -> "Requested action not taken: file name not allowed";
            default -> "Unknown response code";
        };
    }
}