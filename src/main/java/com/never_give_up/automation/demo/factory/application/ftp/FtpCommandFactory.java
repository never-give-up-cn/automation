package com.never_give_up.automation.demo.factory.application.ftp;

import java.nio.charset.StandardCharsets;

/**
 * FTP命令工厂 - 只负责FTP命令的构造
 */
public class FtpCommandFactory {

    public enum FtpCommand {
        USER, PASS, LIST, RETR, STOR,
        PORT, PASV, QUIT, SYST, TYPE,
        CWD, PWD, MKD, RMD, DELE, NOOP
    }

    public byte[] buildCommand(FtpCommand cmd, String param) {
        String cmdLine = cmd.name() + (param != null && !param.isEmpty() ? " " + param : "") + "\r\n";
        return cmdLine.getBytes(StandardCharsets.ISO_8859_1);
    }

    public byte[] buildUserCommand(String username) {
        return buildCommand(FtpCommand.USER, username);
    }

    public byte[] buildPassCommand(String password) {
        return buildCommand(FtpCommand.PASS, password);
    }

    public byte[] buildListCommand() {
        return buildCommand(FtpCommand.LIST, null);
    }

    public byte[] buildRetrCommand(String filename) {
        return buildCommand(FtpCommand.RETR, filename);
    }

    public byte[] buildStorCommand(String filename) {
        return buildCommand(FtpCommand.STOR, filename);
    }

    public byte[] buildPasvCommand() {
        return buildCommand(FtpCommand.PASV, null);
    }

    public byte[] buildQuitCommand() {
        return buildCommand(FtpCommand.QUIT, null);
    }

    public byte[] buildTypeBinaryCommand() {
        return "TYPE I\r\n".getBytes(StandardCharsets.ISO_8859_1);
    }

    // 新增：buildTypeAsciiCommand 方法
    public byte[] buildTypeAsciiCommand() {
        return "TYPE A\r\n".getBytes(StandardCharsets.ISO_8859_1);
    }
}