package com.never_give_up.automation.scanner;

import com.never_give_up.automation.scanner.PortScanResult.PortInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 端口扫描服务
 * <p>
 * 支持：ICMP ping 主机发现、TCP connect 端口扫描、服务指纹识别
 * </p>
 */
@Service
public class PortScannerService {

    private static final Logger log = LoggerFactory.getLogger(PortScannerService.class);

    // ========== 默认配置 ==========

    /** 默认超时（毫秒） */
    private static final int DEFAULT_TIMEOUT_MS = 2000;

    /** ICMP ping 超时 */
    private static final int PING_TIMEOUT_MS = 3000;

    /** Banner 读取超时 */
    private static final int BANNER_READ_TIMEOUT_MS = 2000;

    /** Banner 最大读取字节数 */
    private static final int MAX_BANNER_BYTES = 2048;

    /** 默认并发线程数 */
    private static final int DEFAULT_THREADS = 50;

    /** 常见端口列表（按服务分组） */
    public static final Map<Integer, String> COMMON_PORTS = new LinkedHashMap<>();
    static {
        // 文件传输
        COMMON_PORTS.put(21,    "ftp");
        COMMON_PORTS.put(22,    "ssh");
        COMMON_PORTS.put(23,    "telnet");
        COMMON_PORTS.put(69,    "tftp");
        // 邮件
        COMMON_PORTS.put(25,    "smtp");
        COMMON_PORTS.put(110,   "pop3");
        COMMON_PORTS.put(143,   "imap");
        COMMON_PORTS.put(465,   "smtps");
        COMMON_PORTS.put(587,   "smtp-submission");
        COMMON_PORTS.put(993,   "imaps");
        COMMON_PORTS.put(995,   "pop3s");
        // Web
        COMMON_PORTS.put(80,    "http");
        COMMON_PORTS.put(443,   "https");
        COMMON_PORTS.put(8080,  "http-proxy");
        COMMON_PORTS.put(8443,  "https-alt");
        COMMON_PORTS.put(8888,  "http-alt");
        COMMON_PORTS.put(9090,  "http-admin");
        // 远程管理
        COMMON_PORTS.put(3389,  "rdp");
        COMMON_PORTS.put(5900,  "vnc");
        COMMON_PORTS.put(5901,  "vnc-1");
        COMMON_PORTS.put(5985,  "winrm-http");
        COMMON_PORTS.put(5986,  "winrm-https");
        // 数据库
        COMMON_PORTS.put(3306,  "mysql");
        COMMON_PORTS.put(5432,  "postgresql");
        COMMON_PORTS.put(1521,  "oracle");
        COMMON_PORTS.put(1433,  "mssql");
        COMMON_PORTS.put(27017, "mongodb");
        COMMON_PORTS.put(6379,  "redis");
        COMMON_PORTS.put(6380,  "redis-tls");
        COMMON_PORTS.put(9042,  "cassandra");
        // 消息队列
        COMMON_PORTS.put(5672,  "amqp");
        COMMON_PORTS.put(15672, "rabbitmq-admin");
        COMMON_PORTS.put(9092,  "kafka");
        // 搜索
        COMMON_PORTS.put(9200,  "elasticsearch");
        COMMON_PORTS.put(9300,  "elasticsearch-cluster");
        // 名称服务
        COMMON_PORTS.put(53,    "dns");
        COMMON_PORTS.put(5353,  "mdns");
        // 时间服务
        COMMON_PORTS.put(123,   "ntp");
        // 网络管理
        COMMON_PORTS.put(161,   "snmp");
        COMMON_PORTS.put(162,   "snmptrap");
        // 代理
        COMMON_PORTS.put(1080,  "socks5");
        COMMON_PORTS.put(3128,  "squid-http");
        COMMON_PORTS.put(8081,  "http-alt2");
        // 容器编排
        COMMON_PORTS.put(2375,  "docker");
        COMMON_PORTS.put(2376,  "docker-tls");
        COMMON_PORTS.put(6443,  "kubernetes");
        COMMON_PORTS.put(10250, "kubelet");
        // NFS
        COMMON_PORTS.put(111,   "rpcbind");
        COMMON_PORTS.put(2049,  "nfs");
        // 日志
        COMMON_PORTS.put(514,   "syslog");
        COMMON_PORTS.put(5514,  "syslog-tls");
        // Git
        COMMON_PORTS.put(9418,  "git");
        COMMON_PORTS.put(7990,  "bitbucket");
        // LDAP
        COMMON_PORTS.put(389,   "ldap");
        COMMON_PORTS.put(636,   "ldaps");
        // 其他基础设施
        COMMON_PORTS.put(873,   "rsync");
        COMMON_PORTS.put(2181,  "zookeeper");
        COMMON_PORTS.put(8086,  "influxdb");
        COMMON_PORTS.put(11211, "memcached");
    }

    /** 已知 Banner ↔ 服务映射（用于 banner 抓取后的精确识别） */
    private static final List<BannerPattern> BANNER_PATTERNS = new ArrayList<>();
    static {
        BANNER_PATTERNS.add(new BannerPattern("SSH-\\d+\\.\\d+", "ssh"));
        BANNER_PATTERNS.add(new BannerPattern("220.*FTP", "ftp"));
        BANNER_PATTERNS.add(new BannerPattern("220.*vsFTPd", "ftp", "vsFTPd"));
        BANNER_PATTERNS.add(new BannerPattern("220.*FileZilla", "ftp", "FileZilla"));
        BANNER_PATTERNS.add(new BannerPattern("HTTP/\\d\\.\\d\\s+\\d+", "http"));
        BANNER_PATTERNS.add(new BannerPattern("nginx", "http", "nginx"));
        BANNER_PATTERNS.add(new BannerPattern("Apache", "http", "Apache HTTPD"));
        BANNER_PATTERNS.add(new BannerPattern("Microsoft-IIS", "http", "IIS"));
        BANNER_PATTERNS.add(new BannerPattern("MySQL", "mysql", "MySQL"));
        BANNER_PATTERNS.add(new BannerPattern("MariaDB", "mysql", "MariaDB"));
        BANNER_PATTERNS.add(new BannerPattern("PostgreSQL", "postgresql", "PostgreSQL"));
        BANNER_PATTERNS.add(new BannerPattern("Redis server", "redis", "Redis"));
        BANNER_PATTERNS.add(new BannerPattern("SSH-", "ssh"));
        BANNER_PATTERNS.add(new BannerPattern("220.*SMTP", "smtp"));
        BANNER_PATTERNS.add(new BannerPattern("ESMTP", "smtp"));
    }

    private record BannerPattern(String regex, String serviceName, String... product) {
        public java.util.regex.Pattern compiled() {
            return java.util.regex.Pattern.compile(regex, java.util.regex.Pattern.CASE_INSENSITIVE);
        }
    }

    // ========== 公开 API ==========

    /**
     * 扫描整个子网的主机和端口
     *
     * @param subnet     子网 CIDR，例如 "192.168.1.0/24"
     * @param ports      要扫描的端口列表（null 则使用 1000 个常见端口）
     * @param timeoutMs  连接超时（毫秒），null 则使用默认值
     * @param threadCount 并发线程数，null 则使用默认值 50
     * @return 所有存活主机的扫描结果
     */
    public List<PortScanResult> scanSubnet(String subnet,
                                           List<Integer> ports,
                                           Integer timeoutMs,
                                           Integer threadCount) {
        long startNs = System.nanoTime();
        List<String> ipList = expandCidr(subnet);
        log.info("开始扫描子网 {}，共 {} 个 IP", subnet, ipList.size());

        int portCount = ports != null && !ports.isEmpty() ? ports.size() : 1000;
        int timeout = timeoutMs != null ? timeoutMs : DEFAULT_TIMEOUT_MS;
        int threads = threadCount != null ? threadCount : DEFAULT_THREADS;

        // 第一步：主机发现 ping sweep
        List<PortScanResult> pingResults = pingSweep(ipList, threads);

        // 只扫描存活主机
        List<PortScanResult> aliveHosts = pingResults.stream()
                .filter(PortScanResult::isAlive)
                .toList();
        log.info("主机发现完成，共 {} 台存活", aliveHosts.size());

        // 第二步：端口扫描（仅存活主机）
        List<PortScanResult> finalResults = portScan(aliveHosts, ports, timeout, threads);

        // 计算统计
        long totalMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);
        int totalAlive = (int) finalResults.stream().filter(PortScanResult::isAlive).count();
        int totalOpenPorts = finalResults.stream().mapToInt(r -> r.getOpenPorts().size()).sum();
        log.info("子网 {} 扫描完成，耗时 {}ms，存活 {} 台，开放端口 {} 个",
                subnet, totalMs, totalAlive, totalOpenPorts);

        return finalResults;
    }

    // ========== 主机发现 ==========

    /**
     * ICMP/TCP ping 扫描
     */
    private List<PortScanResult> pingSweep(List<String> ipList, int threads) {
        ExecutorService executor = Executors.newFixedThreadPool(
                Math.min(threads, ipList.size()));
        List<Future<PortScanResult>> futures = new ArrayList<>();

        for (String ip : ipList) {
            futures.add(executor.submit(() -> pingHost(ip)));
        }
        executor.shutdown();

        List<PortScanResult> results = new ArrayList<>();
        for (Future<PortScanResult> f : futures) {
            try {
                results.add(f.get(10, TimeUnit.SECONDS));
            } catch (Exception e) {
                // 单个任务异常不影响整体
            }
        }
        return results;
    }

    private PortScanResult pingHost(String ip) {
        PortScanResult result = new PortScanResult();
        result.setIp(ip);
        result.setAlive(false);
        result.setPingTimeMs(-1);

        try {
            InetAddress addr = InetAddress.getByName(ip);

            // 尝试 ICMP ping（需要管理员权限）
            long start = System.nanoTime();
            boolean icmpReachable = addr.isReachable(PING_TIMEOUT_MS);
            long pingMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);

            if (icmpReachable) {
                result.setAlive(true);
                result.setPingTimeMs(pingMs);
                return result;
            }

            // ICMP 不可达时尝试 TCP ping（连接常见端口）
            // 依次尝试 80, 443, 22 端口
            for (int port : new int[]{80, 443, 22, 8080, 3389}) {
                start = System.nanoTime();
                if (tcpPing(ip, port, 1500)) {
                    pingMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
                    result.setAlive(true);
                    result.setPingTimeMs(pingMs);
                    return result;
                }
            }
        } catch (UnknownHostException e) {
            log.debug("未知主机: {}", ip);
        } catch (Exception e) {
            log.debug("Ping 异常 {}: {}", ip, e.getMessage());
        }
        return result;
    }

    private boolean tcpPing(String ip, int port, int timeoutMs) {
        try (Socket s = new Socket()) {
            s.connect(new InetSocketAddress(ip, port), timeoutMs);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    // ========== 端口扫描 ==========

    /**
     * 对存活主机进行端口扫描
     */
    private List<PortScanResult> portScan(List<PortScanResult> hosts,
                                          List<Integer> ports,
                                          int timeoutMs,
                                          int threadCount) {
        if (hosts.isEmpty()) return hosts;

        List<Integer> scanPorts = ports != null && !ports.isEmpty()
                ? ports
                : generateDefaultPorts(1000);

        int totalTasks = hosts.size() * scanPorts.size();
        log.info("端口扫描：{} 台主机 × {} 个端口 = {} 任务",
                hosts.size(), scanPorts.size(), totalTasks);

        ExecutorService executor = Executors.newFixedThreadPool(
                Math.min(threadCount, Runtime.getRuntime().availableProcessors() * 4));
        CompletionService<ScannedPort> completionService = new ExecutorCompletionService<>(executor);

        AtomicInteger submitted = new AtomicInteger(0);
        for (PortScanResult host : hosts) {
            for (int port : scanPorts) {
                completionService.submit(() -> scanPort(host.getIp(), port, timeoutMs));
                submitted.incrementAndGet();
            }
        }

        // 收集结果
        Map<String, PortScanResult> resultMap = new ConcurrentHashMap<>();
        for (PortScanResult host : hosts) {
            resultMap.put(host.getIp(), host);
        }

        int received = 0;
        while (received < submitted.get()) {
            try {
                Future<ScannedPort> f = completionService.poll(30, TimeUnit.SECONDS);
                if (f == null) {
                    log.warn("端口扫描任务超时，已接收 {}/{}", received, submitted.get());
                    break;
                }
                ScannedPort sp = f.get();
                if (sp != null) {
                    PortScanResult hostResult = resultMap.get(sp.ip);
                    if (hostResult != null) {
                        hostResult.addPort(sp.portInfo);
                        hostResult.setAlive(true); // 确保标记为存活
                    }
                }
                received++;
            } catch (Exception e) {
                received++;
            }
        }

        executor.shutdownNow();
        log.info("端口扫描完成，共收到 {} 个结果", received);
        return new ArrayList<>(resultMap.values());
    }

    /** 单个端口扫描结果 */
    private record ScannedPort(String ip, PortInfo portInfo) {}

    /**
     * 扫描单个端口
     */
    private ScannedPort scanPort(String ip, int port, int timeoutMs) {
        long start = System.nanoTime();
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(ip, port), timeoutMs);
            long connectMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);

            // 端口开放
            PortInfo info = new PortInfo();
            info.setPort(port);
            info.setProtocol("tcp");
            info.setState("open");
            info.setConnectTimeMs(connectMs);
            info.setServiceName(COMMON_PORTS.getOrDefault(port, "unknown"));

            // 尝试 banner 抓取
            String banner = grabBanner(socket, info.getServiceName());
            if (banner != null && !banner.isEmpty()) {
                info.setBanner(banner);
                refineServiceFingerprint(info, banner);
            }

            return new ScannedPort(ip, info);
        } catch (IOException e) {
            // 端口关闭或不可达
            return null;
        } catch (Exception e) {
            log.trace("扫描异常 {}:{} - {}", ip, port, e.getMessage());
            return null;
        }
    }

    // ========== Banner 抓取与指纹识别 ==========

    /**
     * 抓取端口 banner
     */
    private String grabBanner(Socket socket, String guessedService) {
        try {
            socket.setSoTimeout(BANNER_READ_TIMEOUT_MS);
            // 根据常见服务发送探针
            byte[] probe = buildProbe(guessedService);
            if (probe != null) {
                OutputStream os = socket.getOutputStream();
                os.write(probe);
                os.flush();
            }

            // 读取 banner
            InputStream is = socket.getInputStream();
            byte[] buf = new byte[MAX_BANNER_BYTES];
            int totalRead = 0;
            long deadline = System.currentTimeMillis() + BANNER_READ_TIMEOUT_MS;

            while (totalRead < MAX_BANNER_BYTES && System.currentTimeMillis() < deadline) {
                int remaining = (int) Math.min(MAX_BANNER_BYTES - totalRead,
                        Math.max(1, deadline - System.currentTimeMillis()));
                int n = is.read(buf, totalRead, Math.min(remaining, buf.length - totalRead));
                if (n == -1) break;
                totalRead += n;
            }

            if (totalRead > 0) {
                // 清理不可打印字符
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < totalRead; i++) {
                    byte b = buf[i];
                    if (b >= 32 && b < 127) {
                        sb.append((char) b);
                    } else if (b == '\n' || b == '\r' || b == '\t') {
                        sb.append((char) b);
                    }
                }
                return sb.toString().trim();
            }
        } catch (IOException e) {
            // banner 抓取失败是常见的，不记录日志
        }
        return null;
    }

    /**
     * 根据猜测的服务构造探测数据
     */
    private byte[] buildProbe(String service) {
        if (service == null) return null;
        return switch (service) {
            case "http", "http-proxy", "http-alt", "https-alt", "http-admin" ->
                    "GET / HTTP/1.0\r\n\r\n".getBytes();
            case "https" ->
                    null; // TLS 需要 SSL handshake，这里不处理
            case "smtp", "smtp-submission" ->
                    "EHLO scanner\r\n".getBytes();
            case "pop3" ->
                    "CAPA\r\n".getBytes();
            case "imap" ->
                    "A01 CAPABILITY\r\n".getBytes();
            case "ssh" ->
                    null; // SSH 服务端会主动发送 banner
            case "ftp" ->
                    null; // FTP 服务端会主动发送 banner
            case "mysql" ->
                    null; // MySQL 会主动发送 greeting
            case "redis" ->
                    "PING\r\n".getBytes();
            case "memcached" ->
                    "stats\r\n".getBytes();
            default -> null;
        };
    }

    /**
     * 通过 banner 内容细化服务指纹
     */
    private void refineServiceFingerprint(PortInfo info, String banner) {
        if (banner == null || banner.isEmpty()) return;

        for (BannerPattern bp : BANNER_PATTERNS) {
            java.util.regex.Matcher m = bp.compiled().matcher(banner);
            if (m.find()) {
                info.setServiceName(bp.serviceName());
                if (bp.product() != null && bp.product().length > 0) {
                    info.setServiceProduct(bp.product()[0]);
                } else {
                    // 尝试从 banner 中提取版本信息
                    info.setServiceProduct(extractVersion(banner));
                }
                return;
            }
        }

        // 未匹配到已知模式，从 banner 首行推断
        String firstLine = banner.split("\n|\r\n|\r")[0].trim();
        if (firstLine.length() > 3) {
            info.setServiceProduct(firstLine.substring(0, Math.min(80, firstLine.length())));
        }
    }

    /**
     * 从 banner 中尝试提取版本号
     */
    private String extractVersion(String banner) {
        java.util.regex.Matcher m = java.util.regex.Pattern.compile(
                "([\\w.]+/)?([\\d]+\\.[\\d]+(\\.[\\d]+)?[a-zA-Z0-9_-]*)")
                .matcher(banner);
        if (m.find()) {
            return m.group().trim();
        }
        return null;
    }

    // ========== 工具方法 ==========

    /**
     * 解析 CIDR 格式的子网，返回所有 IP 地址列表
     */
    private List<String> expandCidr(String cidr) {
        List<String> result = new ArrayList<>();
        try {
            String[] parts = cidr.split("/");
            String ipStr = parts[0];
            int prefixLen = parts.length > 1 ? Integer.parseInt(parts[1]) : 24;

            // CIDR 范围校验
            if (prefixLen < 8 || prefixLen > 32) {
                log.warn("不支持的 CIDR 前缀长度 {}，使用 /24", prefixLen);
                prefixLen = 24;
            }

            if (prefixLen == 32) {
                result.add(ipStr);
                return result;
            }

            int ipInt = ipToInt(ipStr);
            int mask = prefixLen == 0 ? 0 : (0xFFFFFFFF << (32 - prefixLen));
            int network = ipInt & mask;
            int broadcast = network | (~mask);
            int hostCount = (1 << (32 - prefixLen)) - 2; // 排除网络地址和广播地址

            // 对于 /31 和 /32 的特殊处理
            if (prefixLen >= 31) {
                for (int i = 0; i < (1 << (32 - prefixLen)); i++) {
                    result.add(intToIp(network + i));
                }
            } else {
                for (int i = 1; i <= hostCount; i++) {
                    result.add(intToIp(network + i));
                }
            }
        } catch (Exception e) {
            log.error("CIDR 解析失败: {}", cidr, e);
        }
        return result;
    }

    private int ipToInt(String ip) {
        String[] octets = ip.split("\\.");
        return (Integer.parseInt(octets[0]) << 24)
                | (Integer.parseInt(octets[1]) << 16)
                | (Integer.parseInt(octets[2]) << 8)
                | Integer.parseInt(octets[3]);
    }

    private String intToIp(int val) {
        return String.format("%d.%d.%d.%d",
                (val >>> 24) & 0xFF,
                (val >>> 16) & 0xFF,
                (val >>> 8) & 0xFF,
                val & 0xFF);
    }

    /**
     * 生成默认扫描端口列表（按常见程度排序取前 N 个）
     */
    private List<Integer> generateDefaultPorts(int count) {
        List<Integer> ports = new ArrayList<>(COMMON_PORTS.keySet());
        // 补充一些非知名端口到指定数量
        if (ports.size() < count) {
            Set<Integer> existing = new HashSet<>(ports);
            // 补充 1-1024 范围内的知名端口
            for (int p = 1; p <= 1024 && ports.size() < count; p++) {
                if (!existing.contains(p)) {
                    ports.add(p);
                    existing.add(p);
                }
            }
            // 补充常见的注册端口
            int[] extraPorts = {1025, 1026, 1027, 1028, 1029, 1030, 1433, 1434,
                    1723, 2000, 2001, 3000, 3001, 3268, 3269, 3307, 3388, 3390,
                    3690, 4000, 4040, 4444, 4500, 5000, 5001, 5003, 5050, 5060,
                    5061, 5100, 5190, 5222, 5223, 5269, 5280, 5443, 5555, 5631,
                    5666, 5667, 5671, 5800, 5801, 5900, 5901, 6000, 6001, 6002,
                    6003, 6666, 6667, 7001, 7002, 7070, 7071, 7777, 8000, 8001,
                    8008, 8009, 8010, 8082, 8083, 8084, 8085, 8086, 8087, 8088,
                    8089, 8090, 8099, 8100, 8181, 8333, 8443, 8580, 8649, 8800,
                    8880, 8883, 8889, 8983, 9000, 9001, 9002, 9042, 9043, 9060,
                    9080, 9088, 9091, 9092, 9100, 9151, 9160, 9191, 9200, 9300,
                    9418, 9443, 9600, 9999, 10000, 10001, 10050, 10051, 11000,
                    11211, 12000, 12345, 13579, 16200, 16379, 16380, 17000,
                    18080, 18081, 19000, 20000, 27017, 27018, 28015, 29015, 30000,
                    32768, 49152, 49153, 49154, 49155, 49156, 49157, 50000, 50070,
                    50075, 50090, 50100, 50200, 51111, 61616};
            for (int p : extraPorts) {
                if (!existing.contains(p) && ports.size() < count) {
                    ports.add(p);
                    existing.add(p);
                }
            }
        }
        if (ports.size() > count) {
            ports = ports.subList(0, count);
        }
        Collections.sort(ports);
        return ports;
    }
}
