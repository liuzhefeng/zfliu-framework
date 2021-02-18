package com.zfliu.framework.test;

import com.zfliu.framework.core.BeanContainer;
import com.zfliu.framework.core.DependencyInject;

public class IOCTest {
    public static void main(String[] args) {
        BeanContainer container = BeanContainer.getInstance();
        container.loadBeans("com.zfliu.framework.ioctest");
        DependencyInject dependencyInject = new DependencyInject(container);
        dependencyInject.dependency();
        container.getBeans();
    }
}
