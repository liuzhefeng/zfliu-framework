package com.zfliu.framework.utils;

import com.sun.org.apache.bcel.internal.generic.LoadClass;
import com.zfliu.framework.enums.BasicEnums;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

@Slf4j
public class ClassUtils {

    /**
     * 获取包下面类名（带包名的类名）集合
     */
    public static Set<Class<?>> extractPackageClass(String packageName) {
        /**
         * 1.获取类加载器
         * */
        ClassLoader classLoader = getClassLoader();
        /**
         * 2.加载资源（包名需要‘/’形式）file:/C:/usr/MyCode/zfliu-framework/target/classes/com/zfliu/framework/ioctest
         * */
        URL url = classLoader.getResource(packageName.replace(".", "/"));
        if (url == null) {
            log.warn(packageName + ": find no place!");
            return null;
        }
        Set<Class<?>> classSet = null;
        if (url.getProtocol().equalsIgnoreCase(BasicEnums.FILE_PROTOCOL.getMessage())) {
            classSet = new HashSet<>();
            File file = new File(url.getPath());
            /**
             * 3.protocol：file
             * 将（包名带类名）的.class文件放入set中
             * */
            extractClassFile(classSet, packageName, file);
        }
        return classSet;
    }

    /**
     * 获取类加载器
     */
    public static ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    /**
     * 3.protocol：file
     * 将（包名带类名）的.class文件放入set中
     */
    public static void extractClassFile(Set<Class<?>> classSet, String packageName, File fileSource) {
        if (!fileSource.isDirectory()) {
            return;
        }
        /**
         * listFiles获取文件夹下面的文件和文件夹
         * */
        File[] files = fileSource.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                if (file.isDirectory()) {
                    return true;
                } else {
                    //获取文件的绝对值路径
                    String absolutePath = file.getAbsolutePath();
                    if (absolutePath.endsWith(".class")) {
                        //若是class文件，则直接加载
                        addToClassSet(absolutePath);
                    }
                }
                return false;
            }

            private void addToClassSet(String absolutePath) {
                //1.从class文件的绝对值路径里提取包含了package的类名
                absolutePath = absolutePath.replace(File.separator, ".");
                String className = absolutePath.substring(absolutePath.indexOf(packageName));
                className = className.substring(0, className.lastIndexOf("."));
                //2.通过反射对象
                Class<?> targetClass = loadClass(className);
                classSet.add(targetClass);
            }
        });
        if (files != null) {
            for (File file : files) {
                extractClassFile(classSet, packageName, file);
            }
        }
    }


    /**
     * 反射加载类
     */
    private static Class<?> loadClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            log.error("反射不出来" + e);
            throw new RuntimeException(e);
        }
    }

    public static <T> T newInstance(Class<?> clazz, boolean accessible) {
        try {
            Constructor constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(accessible);
            return (T) constructor.newInstance();
        } catch (Exception e) {
            log.warn("反射创建对象失败");
            throw new RuntimeException(e);
        }
    }

    public static void setField(Field field, Object fieldBean, Object fieldInstance, boolean accessible) {
        field.setAccessible(accessible);
        try {
            field.set(fieldBean, fieldInstance);
        } catch (IllegalAccessException e) {
            log.warn("实例化变量失败、、、、");
        }
    }
}
