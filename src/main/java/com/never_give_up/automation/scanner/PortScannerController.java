package com.never_give_up.automation.scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 端口扫描 REST 控制器
 */
@RestController
@RequestMapping("/api/scan")
public class PortScannerController {

    private static final Logger log = LoggerFactory.getLogger(PortScannerController.class);

    @Autowired
    private PortScannerService scannerService;

    /**
     * 扫描子网
     * <p>
     * GET /api/scan/subnet?subnet=192.168.1.0/24&ports=22,80,443&timeout=2000&threads=50
     * </p>
     */
    @GetMapping("/subnet")
    public ResponseEntity<?> scanSubnet(
            @RequestParam(defaultValue = "192.168.1.0/24") String subnet,
            @RequestParam(required = false) List<Integer> ports,
            @RequestParam(required = false) Integer timeout,
            @RequestParam(required = false) Integer threads) {

        log.info("收到子网扫描请求: subnet={}, ports={}, timeout={}, threads={}",
                subnet, ports, timeout, threads);

        long start = System.currentTimeMillis();
        List<PortScanResult> results = scannerService.scanSubnet(subnet, ports, timeout, threads);
        long elapsed = System.currentTimeMillis() - start;

        // 统计
        long aliveCount = results.stream().filter(PortScanResult::isAlive).count();
        long openPorts = results.stream().mapToLong(r -> r.getOpenPorts().size()).sum();

        return ResponseEntity.ok(Map.of(
                "subnet", subnet,
                "totalHosts", results.size(),
                "aliveHosts", aliveCount,
                "totalOpenPorts", openPorts,
                "durationMs", elapsed,
                "hosts", results
        ));
    }

    /**
     * 扫描单个主机
     * <p>
     * GET /api/scan/host?ip=192.168.1.1&ports=22,80,443&timeout=2000
     * </p>
     */
    @GetMapping("/host")
    public ResponseEntity<?> scanHost(
            @RequestParam(defaultValue = "192.168.1.1") String ip,
            @RequestParam(required = false) List<Integer> ports,
            @RequestParam(required = false) Integer timeout,
            @RequestParam(required = false) Integer threads) {

        log.info("收到主机扫描请求: ip={}, ports={}, timeout={}",
                ip, ports, timeout);

        // 使用 /32 模式扫描单个主机
        List<PortScanResult> results = scannerService.scanSubnet(
                ip + "/32", ports, timeout, threads);

        if (results.isEmpty()) {
            return ResponseEntity.ok(Map.of(
                    "ip", ip,
                    "alive", false,
                    "error", "无法解析主机"
            ));
        }

        return ResponseEntity.ok(results.get(0));
    }

    /**
     * 获取常见端口列表
     */
    @GetMapping("/ports")
    public ResponseEntity<?> getCommonPorts() {
        return ResponseEntity.ok(Map.of(
                "count", PortScannerService.COMMON_PORTS.size(),
                "ports", PortScannerService.COMMON_PORTS
        ));
    }
}
