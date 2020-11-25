package com.qzi.leakprocess.lifecycle;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

/**
 * liveData的子类，用于处理一些生命周期回调逻辑
 * @param <T>
 */
public class LeakProcessLiveData<T> extends MutableLiveData<T> {

    private LifecycleOwner lifecycleOwner;

    public LeakProcessLiveData(LifecycleOwner lifecycleOwner) {
        this.lifecycleOwner = lifecycleOwner;
    }

    @Override
    public void removeObserver(@NonNull Observer<? super T> observer) {

        //当监听者是我们定义的LifecycleObserver对象，则需要在removeObserver逻辑走reset逻辑，避免内存泄露
        if(observer instanceof LeakProcessObserver){
            ((LeakProcessObserver) observer).reset();
        }
        super.removeObserver(observer);
        lifecycleOwner = null;
    }

    public void observe(@NonNull Observer<? super T> observer) {
        super.observe(lifecycleOwner, observer);
    }
}
