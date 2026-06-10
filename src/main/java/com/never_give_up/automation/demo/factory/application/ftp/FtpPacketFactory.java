package com.never_give_up.automation.demo.factory.application.ftp;

public class FtpPacketFactory {
    private String currentUser;
    private boolean isLogin = false;

    /** 构建FTP命令报文 */
    public byte[] buildFtpCommand(String cmd) {
        String content = cmd + "\r\n";
        return content.getBytes();
    }

    /** 构建FTP响应报文 */
    public byte[] buildFtpResponse(int code, String msg) {
        String resp = code + " " + msg + "\r\n";
        return resp.getBytes();
    }

    public void setLogin(String user) {
        this.currentUser = user;
        this.isLogin = true;
    }

    public void reset() {
        currentUser = null;
        isLogin = false;
    }
}