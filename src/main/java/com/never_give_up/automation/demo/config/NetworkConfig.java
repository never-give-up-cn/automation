package com.never_give_up.automation.demo.config;

public class NetworkConfig {
    // 网络参数
    public static final int MTU = 1500;
    public static final int DEFAULT_TTL = 64;
    public static final int MAX_ROUTER_HOPS = 30;
    public static final int WAN_BOTTLE_NECK_MAX = 20;
    public static final int TOTAL_DATA_TO_TRANSMIT = 15;

    // TCP参数
    public static final int INITIAL_CWND = 1;
    public static final int INITIAL_SSTHRESH = 12;
    public static final int RTO_TIMEOUT_MS = 500000;
    public static final int PROBE_INTERVAL_MS = 3000;
    public static final int SERVER_BUFFER_MAX = 5;

    // 超时参数
    public static final long DNS_TIMEOUT_MS = 10000;
    public static final long CONNECTION_TIMEOUT_MS = 300000;
    public static final long CLOSE_WAIT_MS = 5000;

    // 经济参数
    public static final int PRICE_MINER = 30;
    public static final int PRICE_MACHINE = 20;
    public static final int PRICE_UPGRADE_SERVER = 400;

    // 地图参数
    public static final int TILE_SIZE = 40;
    public static final int MAP_COLS = 55;
    public static final int MAP_ROWS = 20;

    // 缩放参数
    public static final double MIN_SCALE = 0.5;
    public static final double MAX_SCALE = 3.0;
    public static final double SCALE_STEP = 0.1;
}
