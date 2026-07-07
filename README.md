# Automation

基于 Spring Boot 的物联网自动化与网络协议模拟系统。

## 项目简介

本项目是一个集**家庭自动化**与**网络协议模拟**于一体的实验性项目。它通过 WebSocket 与 HomeAssistant 集成，同时内置了丰富的网络协议数据包工厂体系，支持多种协议的数据包构建与处理，并包含物流模拟等游戏化模块。

## 技术栈

- **框架**: Spring Boot 3.5.6
- **语言**: Java 17
- **构建工具**: Maven (Maven Wrapper)
- **依赖**:
  - Spring Boot Web
  - Lombok
  - Java-WebSocket
  - Spring Boot Test

## 项目结构

```
src/main/java/com/never_give_up/automation/
├── AutomationApplication.java          # 应用入口
├── Component/
│   └── SMSComponent.java               # 短信组件
├── Config/
│   └── HomeAssistantWebSocketConfig.java # HomeAssistant WebSocket 配置
└── demo/
    ├── adapter/          # 适配器模式实现
    │   └── PacketAdapter.java
    │   └── FactoryManager.java
    ├── animation/        # 动画/数据展示
    │   └── DataCart.java
    ├── config/           # 网络配置
    │   └── NetworkConfig.java
    ├── conveyor/         # 传送带物流系统
    │   └── BeltNetwork.java
    ├── core/             # 核心网络接口与常量
    │   └── INetworkFactory.java
    │   └── ProtocolConst.java
    ├── engine/           # 游戏引擎与地图管理
    │   └── GameEngine.java
    │   └── MapManager.java
    ├── enum1/            # 建筑区枚举
    │   └── BuildingZone.java
    └── factory/          # 网络协议数据包工厂
        ├── address/      # 地址类工厂 (IP, MAC, Port, FiveTuple)
        └── application/  # 应用层协议工厂
            ├── auth/     # 认证协议 (Diameter, Radius)
            ├── dhcp/     # DHCP 租约
            ├── dns/      # DNS 区域
            ├── ftp/      # FTP 协议 (认证, 命令, 数据传输, 响应解析)
            └── http/     # HTTP 协议
```

## 功能特性

- **HomeAssistant 集成**: 通过 WebSocket 连接 HomeAssistant，实现设备自动化控制
- **网络协议模拟**: 支持 DHCP、DNS、FTP、HTTP、Radius、Diameter 等多种协议的数据包构建与处理
- **数据物流系统**: 可视化的数据包传输模拟，包含传送带系统
- **适配器架构**: 灵活的工厂 + 适配器模式设计，易于扩展新协议

## 快速开始

### 前置条件

- JDK 17+
- Maven 3.6+ (或使用项目自带的 `mvnw`)

### 编译运行

```bash
# 编译
./mvnw clean compile

# 运行测试
./mvnw test

# 启动应用
./mvnw spring-boot:run
```

## 许可证

MIT
