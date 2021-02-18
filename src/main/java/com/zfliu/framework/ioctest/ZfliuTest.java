package com.zfliu.framework.ioctest;

import com.zfliu.framework.annotation.Autowired;
import com.zfliu.framework.annotation.Service;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Service
public class ZfliuTest {
    private String name;
    private Integer age;
    @Autowired
    private ZfliuTest2 zfliu;
}
