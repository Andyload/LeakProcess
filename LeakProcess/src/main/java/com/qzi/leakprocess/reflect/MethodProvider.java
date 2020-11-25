package com.qzi.leakprocess.reflect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 方法提供者，用于保持要执行的方法对象和参数
 */
public class MethodProvider {

    //方法对象
    Method method;
    //方法参数
    Object[] args;

    public MethodProvider(Method method) {
        this.method = method;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    public void invoke(Object object) throws InvocationTargetException, IllegalAccessException {
        if(object != null){
            method.invoke(object, args);
        }
    }
}
