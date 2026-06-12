package com.never_give_up.automation.demo.factory.application.ftp;

/**
 * FTP文件类型工厂 - 处理传输模式
 */
public class FtpTransferModeFactory {

    public enum FileType {
        ASCII,      // TYPE A - 文本文件
        BINARY,     // TYPE I - 二进制文件
        EBCDIC,     // TYPE E
        LOCAL       // TYPE L
    }

    public enum TransferMode {
        STREAM,     // 流模式
        BLOCK,      // 块模式
        COMPRESSED  // 压缩模式
    }

    public byte[] buildTypeCommand(FileType type) {
        char typeChar = switch(type) {
            case ASCII -> 'A';
            case BINARY -> 'I';
            case EBCDIC -> 'E';
            case LOCAL -> 'L';
        };
        return ("TYPE " + typeChar + "\r\n").getBytes();
    }
}