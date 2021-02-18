package com.zfliu.framework.ioctest.impl;

import com.zfliu.framework.annotation.Service;

@Service
public class Zfliu implements ZfliuImpl {
    @Override
    public void say() {
        System.out.println("say hello...........");
    }
}
