package com.never_give_up.automation.demo.model;

public enum PacketClass {
    CONTROL_FAST,      // DNS、DHCP、ICMP 控制包 - 最快路径
    TCP_CONTROL,       // SYN、ACK、FIN - 中等路径
    TCP_DATA,          // 普通数据 - 完整路径
    UDP_DATA           // UDP 数据 - 跳过 TCP 层
}
