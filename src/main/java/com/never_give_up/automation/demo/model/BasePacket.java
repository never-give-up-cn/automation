package com.never_give_up.automation.demo.model;

import lombok.Data;
import java.util.List;

@Data
public abstract class BasePacket {
    protected String packetType;
    protected byte[] payload;
    protected List<String> layerStack;

    public abstract byte[] serialize();
    public abstract void deserialize(byte[] data);
}