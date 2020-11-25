package com.qzi.leakprocess.lifecycle;

import androidx.lifecycle.Observer;

/**
 * LiveData监听器
 * @param <T>
 */
public interface LeakProcessObserver<T> extends Observer<T> {
    /**
     * 用于处理当移除监听器时的重置工作
     */
    void reset();
}
