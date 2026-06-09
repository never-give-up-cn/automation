package com.never_give_up.automation.demo.core;

public interface INetworkFactory<T> {
    T produce();
    void reset();
}
