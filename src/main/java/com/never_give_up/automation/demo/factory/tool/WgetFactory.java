package com.never_give_up.automation.demo.factory.tool;

/** Wget 文件下载工具 */
public class WgetFactory {
    private String savePath = "./download/";

    public byte[] buildDownloadRequest(String url) {
        String req = "GET " + url + " HTTP/1.1\r\n\r\n";
        return req.getBytes();
    }

    public void setSavePath(String path) {
        this.savePath = path;
    }

    public void reset() {
        savePath = "./download/";
    }
}