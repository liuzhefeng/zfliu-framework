package com.zfliu.framework.utils;

import java.util.Collection;

public class ValidatedUtils {
    /**
     * Collection是否为null或size为0
     *
     * @param obj Collection
     * @return 是否为空
     */
    public static boolean isEmpty(Collection<?> obj){
        return obj == null || obj.isEmpty();
    }


}
