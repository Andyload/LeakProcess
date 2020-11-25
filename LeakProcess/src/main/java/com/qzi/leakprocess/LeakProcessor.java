package com.qzi.leakprocess;

import androidx.lifecycle.LifecycleOwner;

import com.qzi.leakprocess.lifecycle.LeakProcessLiveData;
import com.qzi.leakprocess.reflect.LeakProcessInvocationHandler;
import com.qzi.leakprocess.reflect.MethodProvider;

import java.lang.reflect.Proxy;

/**
 * 生命周期提供者。针对接口，
 * 可以处理在Activity，Fragment内部实现的接口
 * 根据LiveData的生命周期去处理接口实现类和Activity，Fragment之间的引用关系
 * 可以有效避免内存泄露
 */
public class LeakProcessor{

    /**
     * 包裹方法，用于返回一个接口的代理类
     * @param lifecycleOwner    Activity的生命周期所有者
     * @param t     接口的实现类
     * @param <T>   接口的泛型
     * @return      返回该接口的代理类
     */
    public static  <T> T wrap(LifecycleOwner lifecycleOwner, T t){
        LeakProcessInvocationHandler lifecycleInvocationHandlerImp = new LeakProcessInvocationHandler(t, new LeakProcessLiveData<MethodProvider>(lifecycleOwner));
        return (T) Proxy.newProxyInstance(t.getClass().getClassLoader(), t.getClass().getInterfaces(), lifecycleInvocationHandlerImp);
    }

}
