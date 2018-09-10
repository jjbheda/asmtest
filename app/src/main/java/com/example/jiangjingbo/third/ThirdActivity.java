package com.example.jiangjingbo.third;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.jiangjingbo.asmtest.R;

public class ThirdActivity extends Activity{

    Button btn_thr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thrid);
        btn_thr = findViewById(R.id.btn_third);
        btn_thr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                   e.printStackTrace();
                }
            }
        });
    }
}
