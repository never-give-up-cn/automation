package com.never_give_up.automation.demo.factory.application.ftp;

/**
 * FTP认证工厂 - 处理登录认证
 */
public class FtpAuthFactory {

    private String username;
    private boolean authenticated = false;

    public boolean processAuthResponse(int responseCode, String message) {
        switch(responseCode) {
            case 331:  // 需要密码
                return false;
            case 230:  // 登录成功
                authenticated = true;
                return true;
            case 530:  // 登录失败
                authenticated = false;
                return false;
            default:
                return false;
        }
    }

    public byte[] buildLoginSequence(String user, String pass) {
        return null;
    }

    // 新增：setAuthenticated 方法
    public void setAuthenticated(boolean auth) {
        this.authenticated = auth;
    }

    // 新增：reset 方法
    public void reset() {
        this.username = null;
        this.authenticated = false;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public String getUsername() {
        return username;
    }
}