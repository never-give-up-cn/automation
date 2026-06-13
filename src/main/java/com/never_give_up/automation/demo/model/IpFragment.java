package com.never_give_up.automation.demo.model;

public class IpFragment {
    public int offset;
    public boolean moreFragments;
    public byte[] data;

    public IpFragment(int offset, boolean mf, byte[] data) {
        this.offset = offset;
        this.moreFragments = mf;
        this.data = data;
    }
}
