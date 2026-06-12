package com.never_give_up.automation.demo.factory.application.ftp;

/**
 * FTP数据通道工厂 - 处理PORT/PASV模式
 * 注意：这个工厂只是计算端口信息，实际TCP连接由TCP工厂处理
 */
public class FtpDataChannelFactory {

    private boolean passiveMode = false;
    private int dataPort = 20;  // FTP数据端口默认20

    /**
     * 构建PORT命令 - 告诉服务器客户端的数据端口
     * 格式: PORT h1,h2,h3,h4,p1,p2
     */
    public String buildPortCommand(String ip, int port) {
        String[] ipParts = ip.split("\\.");
        int p1 = port / 256;
        int p2 = port % 256;
        return String.format("PORT %s,%d,%d",
                String.join(",", ipParts), p1, p2);
    }

    /**
     * 解析PASV响应 - 获取服务器数据端口
     * 响应格式: 227 Entering Passive Mode (h1,h2,h3,h4,p1,p2)
     * 例如: "227 Entering Passive Mode (192,168,1,100,234,150)"
     *      端口 = 234 * 256 + 150 = 60054
     *
     * @param pasvResponse 服务器返回的PASV响应字符串
     * @return 服务器开放的数据端口号，解析失败返回-1
     */
    public int parsePassivePort(String pasvResponse) {
        if (pasvResponse == null || pasvResponse.isEmpty()) {
            return -1;
        }

        try {
            // 查找括号内的内容
            int startBracket = pasvResponse.indexOf('(');
            int endBracket = pasvResponse.indexOf(')');

            if (startBracket == -1 || endBracket == -1) {
                // 尝试匹配其他格式: "227 =h1,h2,h3,h4,p1,p2"
                startBracket = pasvResponse.indexOf('=');
                if (startBracket != -1) {
                    startBracket = startBracket + 1;
                } else {
                    return -1;
                }
            } else {
                startBracket++;
            }

            // 提取括号内的数字部分
            String numbersStr = pasvResponse.substring(startBracket, endBracket);
            String[] numbers = numbersStr.split(",");

            if (numbers.length < 6) {
                return -1;
            }

            // 最后两个数字是端口的高8位和低8位
            int p1 = Integer.parseInt(numbers[4].trim());
            int p2 = Integer.parseInt(numbers[5].trim());

            // 计算端口号: p1 * 256 + p2
            int port = p1 * 256 + p2;

            // 验证端口范围（1-65535）
            if (port < 1 || port > 65535) {
                return -1;
            }

            this.dataPort = port;
            this.passiveMode = true;

            return port;

        } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
            // 解析失败，返回-1
            return -1;
        }
    }

    /**
     * 解析PORT命令中的端口信息（作为服务器端使用）
     * 从PORT命令中提取客户端指定的数据端口
     *
     * @param portCommand PORT命令字符串，格式: "PORT 192,168,1,100,123,45"
     * @return 客户端指定的端口号，解析失败返回-1
     */
    public int parsePortCommand(String portCommand) {
        if (portCommand == null || !portCommand.startsWith("PORT")) {
            return -1;
        }

        try {
            // 提取PORT后面的参数部分
            String paramPart = portCommand.substring(5).trim();
            String[] numbers = paramPart.split(",");

            if (numbers.length < 6) {
                return -1;
            }

            // 最后两个数字是端口的高8位和低8位
            int p1 = Integer.parseInt(numbers[4].trim());
            int p2 = Integer.parseInt(numbers[5].trim());

            // 移除可能存在的回车换行符
            String lastNum = numbers[5].trim();
            int crIndex = lastNum.indexOf('\r');
            if (crIndex != -1) {
                p2 = Integer.parseInt(lastNum.substring(0, crIndex));
            }

            int port = p1 * 256 + p2;

            if (port < 1 || port > 65535) {
                return -1;
            }

            this.dataPort = port;
            this.passiveMode = false;

            return port;

        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * 构建PASV命令
     * 请求服务器进入被动模式
     */
    public byte[] buildPasvCommand() {
        return "PASV\r\n".getBytes();
    }

    /**
     * 获取服务器响应码
     * 检查PASV响应是否为成功响应（227）
     *
     * @param pasvResponse 服务器响应
     * @return 是否是被动模式成功响应
     */
    public boolean isPasvResponse(byte[] pasvResponse) {
        if (pasvResponse == null || pasvResponse.length < 3) {
            return false;
        }
        String response = new String(pasvResponse);
        return response.startsWith("227");
    }

    /**
     * 构建EPSV命令（IPv6扩展被动模式）
     * 用于IPv6环境下的被动模式
     *
     * @param protocol 协议类型: 1=IPv4, 2=IPv6
     */
    public byte[] buildEpsvCommand(int protocol) {
        return ("EPSV " + protocol + "\r\n").getBytes();
    }

    /**
     * 解析EPSV响应
     * 响应格式: 229 Entering Extended Passive Mode (|||port|)
     *
     * @param epsvResponse EPSV响应字符串
     * @return 数据端口号，解析失败返回-1
     */
    public int parseEpsvPort(String epsvResponse) {
        if (epsvResponse == null || !epsvResponse.startsWith("229")) {
            return -1;
        }

        try {
            // 查找 |||port| 格式
            int startPipe = epsvResponse.indexOf("|||");
            if (startPipe == -1) {
                return -1;
            }

            int endPipe = epsvResponse.indexOf('|', startPipe + 3);
            if (endPipe == -1) {
                return -1;
            }

            String portStr = epsvResponse.substring(startPipe + 3, endPipe);
            int port = Integer.parseInt(portStr);

            if (port < 1 || port > 65535) {
                return -1;
            }

            this.dataPort = port;
            this.passiveMode = true;

            return port;

        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * 获取当前数据端口
     */
    public int getDataPort() {
        return dataPort;
    }

    /**
     * 是否处于被动模式
     */
    public boolean isPassiveMode() {
        return passiveMode;
    }

    /**
     * 设置被动模式
     */
    public void setPassiveMode(boolean passiveMode) {
        this.passiveMode = passiveMode;
    }

    /**
     * 重置工厂状态
     */
    public void reset() {
        passiveMode = false;
        dataPort = 20;
    }

    /**
     * 辅助方法：将IP和端口转换为PORT命令格式
     */
    public static String ipAndPortToPortFormat(String ip, int port) {
        String[] ipParts = ip.split("\\.");
        int p1 = port / 256;
        int p2 = port % 256;
        return String.format("%s,%d,%d", String.join(",", ipParts), p1, p2);
    }

    /**
     * 辅助方法：从PORT格式解析IP和端口
     */
    public static String[] parseIpAndPortFromPortFormat(String portFormat) {
        String[] numbers = portFormat.split(",");
        if (numbers.length < 6) {
            return null;
        }

        String ip = String.join(".",
                numbers[0], numbers[1], numbers[2], numbers[3]);
        int p1 = Integer.parseInt(numbers[4]);
        int p2 = Integer.parseInt(numbers[5]);
        int port = p1 * 256 + p2;

        return new String[]{ip, String.valueOf(port)};
    }
}