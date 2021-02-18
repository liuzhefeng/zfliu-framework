package com.zfliu.framework.core;

import com.zfliu.framework.annotation.Autowired;
import com.zfliu.framework.utils.ClassUtils;
import com.zfliu.framework.utils.ValidatedUtils;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.Set;

@Slf4j
public class DependencyInject {
    /**
     * Bean容器
     */
    private BeanContainer beanContainer;

    public DependencyInject(BeanContainer beanContainer) {
        this.beanContainer = beanContainer;
    }

    /**
     * DI操作
     */
    public void dependency() {
        Set<Class<?>> classSet = beanContainer.getClasses();
        if (ValidatedUtils.isEmpty(classSet)) {
            log.warn("container中目前没有Bean加载");
            return;
        }
        for (Class<?> clazz : classSet) {
            Field[] fields = clazz.getDeclaredFields();
            if (fields.length == 0 || fields == null) {
                continue;
            }
            for (Field field : fields) {
                if (field.isAnnotationPresent(Autowired.class)) {
                    Autowired autowired = field.getAnnotation(Autowired.class);
                    String autowiredValue = autowired.value();
                    /**
                     * field类型
                     * */
                    Class<?> fieldType = field.getType();
                    /**
                     * 选择容器里对应的实例将成员变量赋值
                     * */
                    Object fieldInstance = getFieldInstance(fieldType, autowiredValue);
                    if (fieldInstance == null) {
                        log.warn(fieldType.getName() + "不能依赖注入" + autowiredValue);
                        return;
                    } else {
                        /**fieldInstance：实例变量
                         * fieldBean：实例变量所在类
                         * */
                        Object fieldBean = beanContainer.getBean(clazz);
                        ClassUtils.setField(field,fieldBean,fieldInstance,true);
                    }
                }
            }
        }
    }

    private Object getFieldInstance(Class<?> fieldType, String autowiredValue) {
        Object fieldValue = beanContainer.getBean(fieldType);
        if (fieldValue != null) {
            return fieldValue;
        } else {
            /**
             * 去继承类或实现类
             * */
            Class<?> implementClass = getImplementClass(fieldType, autowiredValue);
            if (implementClass != null) {
                return beanContainer.getBean(implementClass);
            } else {
                log.warn("容器中未加载此类。。。。。");
                return null;
            }
        }
    }

    private Class<?> getImplementClass(Class<?> fieldType, String autowiredValue) {
        Set<Class<?>> classSet = beanContainer.getClassesSuper(fieldType);
        if (ValidatedUtils.isEmpty(classSet)) {
            return null;
        } else {
            if (autowiredValue == null || autowiredValue == "") {
                return classSet.iterator().next();
            } else {
                for (Class clazz : classSet) {
                    /**
                     * 首字母小写的类
                     * */
                    if (autowiredValue.equals(clazz.getSimpleName())) {
                        return clazz;
                    }
                }
            }
        }
        return null;
    }

}
