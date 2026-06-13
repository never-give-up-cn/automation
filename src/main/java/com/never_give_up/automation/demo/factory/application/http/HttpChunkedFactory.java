package com.never_give_up.automation.demo.factory.application.http;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * HTTP 分块传输编码工厂 - Transfer-Encoding: chunked 编码/解码
 */
public class HttpChunkedFactory {

    public static class Chunk {
        private final int size;
        private final byte[] data;
        private final String extension;

        public Chunk(int size, byte[] data, String extension) {
            this.size = size;
            this.data = data;
            this.extension = extension;
        }

        public int getSize() { return size; }
        public byte[] getData() { return data; }
        public String getExtension() { return extension; }
    }

    /**
     * 编码单个块
     * 格式: chunk-size [;extension]\r\n chunk-data \r\n
     */
    public byte[] encodeChunk(byte[] data) {
        return encodeChunk(data, null);
    }

    /**
     * 编码单个块（带扩展）
     */
    public byte[] encodeChunk(byte[] data, String extension) {
        StringBuilder sb = new StringBuilder();
        sb.append(Integer.toHexString(data.length));
        if (extension != null && !extension.isEmpty()) {
            sb.append(";").append(extension);
        }
        sb.append("\r\n");
        byte[] header = sb.toString().getBytes(StandardCharsets.ISO_8859_1);
        byte[] trailer = "\r\n".getBytes(StandardCharsets.ISO_8859_1);

        byte[] result = new byte[header.length + data.length + trailer.length];
        System.arraycopy(header, 0, result, 0, header.length);
        System.arraycopy(data, 0, result, header.length, data.length);
        System.arraycopy(trailer, 0, result, header.length + data.length, trailer.length);
        return result;
    }

    /**
     * 编码结束块
     * 0\r\n [trailer]\r\n
     */
    public byte[] encodeLastChunk(String... trailerHeaders) {
        StringBuilder sb = new StringBuilder("0\r\n");
        for (String trailer : trailerHeaders) {
            sb.append(trailer).append("\r\n");
        }
        sb.append("\r\n");
        return sb.toString().getBytes(StandardCharsets.ISO_8859_1);
    }

    /**
     * 编码完整的分块消息体
     */
    public byte[] encodeChunkedBody(List<byte[]> chunks) {
        byte[][] encoded = new byte[chunks.size() + 1][];
        int totalLen = 0;
        for (int i = 0; i < chunks.size(); i++) {
            encoded[i] = encodeChunk(chunks.get(i));
            totalLen += encoded[i].length;
        }
        encoded[chunks.size()] = encodeLastChunk();
        totalLen += encoded[chunks.size()].length;

        byte[] result = new byte[totalLen];
        int offset = 0;
        for (byte[] part : encoded) {
            System.arraycopy(part, 0, result, offset, part.length);
            offset += part.length;
        }
        return result;
    }

    /**
     * 解码分块数据
     */
    public List<Chunk> decodeChunks(byte[] data) {
        List<Chunk> chunks = new ArrayList<>();
        if (data == null || data.length == 0) return chunks;

        String text = new String(data, StandardCharsets.ISO_8859_1);
        int pos = 0;

        while (pos < text.length()) {
            // 读取块大小行
            int lineEnd = text.indexOf("\r\n", pos);
            if (lineEnd == -1) break;

            String chunkLine = text.substring(pos, lineEnd);
            pos = lineEnd + 2;

            // 解析块大小 [;extension]
            String sizeStr;
            String extension = null;
            int semi = chunkLine.indexOf(';');
            if (semi >= 0) {
                sizeStr = chunkLine.substring(0, semi);
                extension = chunkLine.substring(semi + 1);
            } else {
                sizeStr = chunkLine;
            }

            int chunkSize;
            try {
                chunkSize = Integer.parseInt(sizeStr.trim(), 16);
            } catch (NumberFormatException e) {
                break;
            }

            // 最后块 (size == 0)
            if (chunkSize == 0) break;

            // 读取块数据
            if (pos + chunkSize > text.length()) break;
            byte[] chunkData = new byte[chunkSize];
            System.arraycopy(data, pos, chunkData, 0, chunkSize);
            pos += chunkSize;

            // 跳过末尾 \r\n
            if (pos + 2 <= text.length()) {
                pos += 2;
            }

            chunks.add(new Chunk(chunkSize, chunkData, extension));
        }

        return chunks;
    }

    /**
     * 解码并合并所有块数据
     */
    public byte[] decodeAndJoin(byte[] data) {
        List<Chunk> chunks = decodeChunks(data);
        int totalSize = 0;
        for (Chunk c : chunks) {
            totalSize += c.getSize();
        }
        byte[] result = new byte[totalSize];
        int offset = 0;
        for (Chunk c : chunks) {
            System.arraycopy(c.getData(), 0, result, offset, c.getSize());
            offset += c.getSize();
        }
        return result;
    }

    /**
     * 判断消息体是否为分块编码
     */
    public boolean isChunked(String transferEncoding) {
        return transferEncoding != null
                && transferEncoding.toLowerCase().contains("chunked");
    }
}
