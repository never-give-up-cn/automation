package com.never_give_up.automation.demo.winModel;

import lombok.Data;

@Data
public class IpFragment {
    private int offset;
    private boolean moreFragments;
    private byte[] data;

    public IpFragment(int offset, boolean mf, byte[] data) {
        this.offset = offset;
        this.moreFragments = mf;
        this.data = data;
    }
}

