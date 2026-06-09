package com.never_give_up.automation.demo.test;
// 所有工厂统一接口
public interface INetworkFactory<T> {
    T produce();       // 生产/构造对象
    void reset();      // 重置工厂状态
}
