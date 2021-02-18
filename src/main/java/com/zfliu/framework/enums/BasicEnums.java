package com.zfliu.framework.enums;

import lombok.Getter;

@Getter
public enum BasicEnums {
    FILE_PROTOCOL("file");
    String message;
    BasicEnums(String message){
        this.message = message;
    }
}
