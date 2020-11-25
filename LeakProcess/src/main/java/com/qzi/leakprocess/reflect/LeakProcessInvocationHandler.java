package com.qzi.leakprocess.reflect;

import android.os.Message;
import android.util.ArrayMap;

import com.qzi.leakprocess.lifecycle.LeakProcessLiveData;
import com.qzi.leakprocess.lifecycle.LeakProcessObserver;
import com.qzi.leakprocess.annotation.Ignore;
import com.qzi.leakprocess.dispatch.ThreadDispatch;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.Queue;

/**
 * 接口代理的主要处理逻辑类
 */
public class LeakProcessInvocationHandler implements InvocationHandler, LeakProcessObserver<MethodProvider> {

    /**
     * 用于存储接口实现类的方法集合（除了用@Ignore修饰的方法）
     */
    ArrayMap<String, MethodProvider> methodProviderMap = new ArrayMap<>();

    /**
     * 用来处理LivaData的回调
     */
    LeakProcessLiveData<MethodProvider> methodProviderLiveData;

    /**
     * 接口实现类
     */
    private Object target;

    /**
     * 方法链表，用来存储将要执行的接口实现类的方法
     */
    private Queue<MethodProvider> methodProviderQueue = new LinkedList<>();

    /**
     * 是否准备在RESUME，START生命周期执行方法
     */
    private boolean isRunning;

    public LeakProcessInvocationHandler(Object target, LeakProcessLiveData<MethodProvider> methodProviderLiveData) {
        this.target = target;
        this.methodProviderLiveData = methodProviderLiveData;
        collectMethod(target);
        methodProviderLiveData.observe(this);
    }

    /**
     * 采集接口实现类的方法（除了用@Ignore修饰的方法）
     * @param target    接口实现类
     */
    private void collectMethod(Object target) {
        Method[] methods = target.getClass().getDeclaredMethods();
        for (Method method : methods) {
            //过滤使用@Ingore注解的方法
            if (!method.isAnnotationPresent(Ignore.class)) {
                methodProviderMap.put(method.getName(), new MethodProvider(method));
            }
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        MethodProvider methodProvider = methodProviderMap.get(method.getName());
        if (methodProvider != null) {
            methodProvider.setArgs(args);
            Message message = Message.obtain(ThreadDispatch.getMainHandler(), new MessageRunnable(methodProvider));
            ThreadDispatch.getMainHandler().sendMessage(message);
        } else if (target != null) {
            method.invoke(target);
        }
        return null;
    }

    @Override
    public void onChanged(MethodProvider methodProvider) {
        try {
            methodProvider.invoke(target);
            if (!methodProviderQueue.isEmpty()) {
                //如果链表不为空，则继续取出方法继续执行
                methodProviderLiveData.setValue(methodProviderQueue.poll());
            } else {
                //如果链表为空，则代表目前没有方法准备执行，则可以把isRunning标示置为false
                isRunning = false;
            }
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void reset() {
        target = null;
        methodProviderMap.clear();
        methodProviderQueue.clear();
    }

    /**
     * 主线程执行的任务，用于方法调用前的处理判断
     */
    private class MessageRunnable implements Runnable {

        private MethodProvider methodProvider;

        public MessageRunnable(MethodProvider methodProvider) {
            this.methodProvider = methodProvider;
        }

        @Override
        public void run() {
            //先判断是否有方法准备执行
            if (isRunning) {
                //如果有，则把当前方法添加到链表中
                methodProviderQueue.add(methodProvider);
            } else {
                //如果没有，则直接调用setValue开始方法的准备执行逻辑
                isRunning = true;
                methodProviderLiveData.setValue(methodProvider);
            }
        }
    }
}
