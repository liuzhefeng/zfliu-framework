package com.zfliu.framework.core;

import com.zfliu.framework.annotation.Controller;
import com.zfliu.framework.annotation.Service;
import com.zfliu.framework.utils.ClassUtils;
import com.zfliu.framework.utils.ValidatedUtils;
import jdk.nashorn.internal.ir.annotations.Ignore;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BeanContainer {
    /**
     * 获取Bean容器实例（单例模式）enum
     */
    public static BeanContainer getInstance() {
        return ContainerHolder.HOLDER.instance;
    }

    private enum ContainerHolder {
        HOLDER;
        private BeanContainer instance;

        ContainerHolder() {
            instance = new BeanContainer();
        }
    }

    /**
     * 扫描并加载所有的Bean
     * ConcurrentHashMap：存放被配置标记的对象的Map
     * List:加载bean的注解列表
     */
    private final Map<Class<?>, Object> map = new ConcurrentHashMap<>();
    private static final List<Class<? extends Annotation>> BeanAnnotation = Arrays.asList(
            Controller.class, Service.class
    );

    public synchronized void loadBeans(String packageName) {
        if (isLoaded()) {
            log.warn("已加载过.....");
            return;
        }
        Set<Class<?>> classSet = ClassUtils.extractPackageClass(packageName);
        if (classSet == null && classSet.isEmpty()) {
            log.warn(packageName + "is empty");
            return;
        }
        for (Class<?> clazz : classSet) {
            for (Class<? extends Annotation> annotation : BeanAnnotation) {
                if (clazz.isAnnotationPresent(annotation)) {
                    map.put(clazz, ClassUtils.newInstance(clazz, true));
                }
            }
        }
        loaded = true;
    }

    /**
     * 判断容器是否加载过Bean
     */
    private boolean loaded = false;

    public boolean isLoaded() {
        return loaded;
    }

    /**
     * CRUD操作
     */
    public Set<Class<?>> getClasses() {
        return map.keySet();
    }

    public Set<Object> getBeans() {
        return new HashSet<>(map.values());
    }

    public Object getBean(Class<?> clazz) {
        return map.get(clazz);
    }
    /**
     * 根据注解得到Bean的class Set
     */
    public Set<Class<?>> getClassesByAnnotation(Class<? extends Annotation> annotation) {
        Set<Class<?>> classSet = getClasses();
        if (ValidatedUtils.isEmpty(classSet)) {
            log.warn("container中目前没有Bean加载");
            return null;
        }
        Set<Class<?>> annotationSet = new HashSet<>();
        for (Class<?> clazz : classSet) {
            if (clazz.isAnnotationPresent(annotation)) {
                annotationSet.add(clazz);
            }
        }
        return annotationSet.size() > 0 ? annotationSet : null;
    }

    /**
     * 根据接口或者父类实现子类的Class集合，不包括本身自己类
     */
    public Set<Class<?>> getClassesSuper(Class<?> interfaceOrClass) {
        Set<Class<?>> classSet = getClasses();
        if (ValidatedUtils.isEmpty(classSet)) {
            log.warn("container中目前没有Bean加载");
            return null;
        }
        Set<Class<?>> superSet = new HashSet<>();
        for (Class<?> clazz : classSet) {
            if (interfaceOrClass.isAssignableFrom(clazz) && !clazz.equals(interfaceOrClass)) {
                superSet.add(clazz);
            }
        }
        return superSet.isEmpty() ? null : superSet;
    }
}
