package com.never_give_up.automation.demo.factory.physical;

import java.util.Random;
public class PhysicalChannelFactory {
    private final Random r = new Random();
    public long getPropagationDelayNano(int distanceMeter) {
        return (long) (distanceMeter * 5.0);
    }
    public double getBER() { return 1e-9; }
    public int getJitterBufferSize() { return 16; }
    public byte[] applyNoise(byte[] stream) {
        if(r.nextDouble() > getBER()) return stream;
        byte[] copy = new byte[stream.length];
        System.arraycopy(stream,0,copy,0,stream.length);
        copy[r.nextInt(copy.length)] ^= 1;
        return copy;
    }
}