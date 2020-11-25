package com.qzi.lifeofinterface;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.qzi.leakprocess.LeakProcessor;
import com.qzi.leakprocess.annotation.Ignore;

public class SecondActivity extends AppCompatActivity implements Callback{

    Pesent pesent;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pesent = new Pesent();
                pesent.start(LeakProcessor.wrap(SecondActivity.this,SecondActivity.this));
            }
        });
    }

    @Override
    public void onBegin() {
        Log.e("SecondActivity", "onBegin: " + Thread.currentThread());
    }

    @Ignore
    @Override
    public void onEnd() {
        Log.e("SecondActivity", "onEnd: " + Thread.currentThread());
    }
}
