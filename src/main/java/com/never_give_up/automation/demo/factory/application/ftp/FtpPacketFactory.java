package com.never_give_up.automation.demo.factory.application.ftp;

import java.nio.charset.StandardCharsets;

/**
 * FTP主工厂 - 门面模式，协调各个子工厂
 */
public class FtpPacketFactory {

    private FtpCommandFactory commandFactory = new FtpCommandFactory();
    private FtpResponseParser responseParser = new FtpResponseParser();
    private FtpDataChannelFactory dataChannelFactory = new FtpDataChannelFactory();
    private FtpAuthFactory authFactory = new FtpAuthFactory();

    private String currentUser;
    private boolean isLogin = false;
    private int lastResponseCode = 0;

    // ========== 命令构建方法 ==========

    public byte[] buildFtpCommand(String cmd) {
        String content = cmd + "\r\n";
        return content.getBytes(StandardCharsets.ISO_8859_1);
    }

    public byte[] buildUserCommand(String username) {
        this.currentUser = username;
        return commandFactory.buildUserCommand(username);
    }

    public byte[] buildPassCommand(String password) {
        return commandFactory.buildPassCommand(password);
    }

    public byte[] buildListCommand() {
        return commandFactory.buildListCommand();
    }

    public byte[] buildRetrCommand(String filename) {
        return commandFactory.buildRetrCommand(filename);
    }

    public byte[] buildStorCommand(String filename) {
        return commandFactory.buildStorCommand(filename);
    }

    public byte[] buildPasvCommand() {
        return commandFactory.buildPasvCommand();
    }

    public byte[] buildQuitCommand() {
        return commandFactory.buildQuitCommand();
    }

    public byte[] buildTypeBinaryCommand() {
        return commandFactory.buildTypeBinaryCommand();
    }

    // ========== 新增：buildTypeAsciiCommand 方法 ==========
    public byte[] buildTypeAsciiCommand() {
        return "TYPE A\r\n".getBytes(StandardCharsets.ISO_8859_1);
    }
    // ========== 新增结束 ==========

    // ========== 响应处理方法 ==========

    public FtpResponseParser.FtpResponse parseResponse(byte[] data) {
        return responseParser.parse(data);
    }

    public int getResponseCode(byte[] data) {
        FtpResponseParser.FtpResponse resp = responseParser.parse(data);
        return resp != null ? resp.getCode() : -1;
    }

    public boolean isLoginSuccess(byte[] response) {
        FtpResponseParser.FtpResponse resp = responseParser.parse(response);
        return resp != null && resp.isLoginSuccess();
    }

    public boolean isNeedPassword(byte[] response) {
        FtpResponseParser.FtpResponse resp = responseParser.parse(response);
        return resp != null && resp.isNeedPassword();
    }

    public boolean isPassiveModeResponse(byte[] response) {
        FtpResponseParser.FtpResponse resp = responseParser.parse(response);
        return resp != null && resp.isPassiveMode();
    }

    public int parsePassivePort(byte[] response) {
        FtpResponseParser.FtpResponse resp = responseParser.parse(response);
        if (resp != null && resp.isPassiveMode()) {
            String[] info = responseParser.extractPassiveInfo(resp);
            if (info != null && info.length >= 2) {
                return Integer.parseInt(info[1]);
            }
        }
        return -1;
    }

    // ========== FTP会话状态管理 ==========

    public void setLogin(String user) {
        this.currentUser = user;
        this.isLogin = true;
        if (authFactory != null) {
            authFactory.setAuthenticated(true);
        }
    }

    public void reset() {
        currentUser = null;
        isLogin = false;
        lastResponseCode = 0;
        if (authFactory != null) {
            authFactory.reset();
        }
        if (dataChannelFactory != null) {
            dataChannelFactory.reset();
        }
    }

    public boolean isLogin() {
        return isLogin;
    }

    public String getCurrentUser() {
        return currentUser;
    }

    public void setLastResponseCode(int code) {
        this.lastResponseCode = code;
    }

    public int getLastResponseCode() {
        return lastResponseCode;
    }

    // ========== 数据通道相关 ==========

    public boolean isPassiveMode() {
        return dataChannelFactory != null && dataChannelFactory.isPassiveMode();
    }

    public int getDataPort() {
        return dataChannelFactory != null ? dataChannelFactory.getDataPort() : -1;
    }

    public void setPassiveMode(boolean passive) {
        if (dataChannelFactory != null) {
            dataChannelFactory.setPassiveMode(passive);
        }
    }

    // 在 FtpPacketFactory.java 中添加

    public FtpCommandFactory getCommandFactory() {
        return commandFactory;
    }

    public FtpResponseParser getResponseParser() {
        return responseParser;
    }

    public FtpDataChannelFactory getDataChannelFactory() {
        return dataChannelFactory;
    }

    public FtpAuthFactory getAuthFactory() {
        return authFactory;
    }
}