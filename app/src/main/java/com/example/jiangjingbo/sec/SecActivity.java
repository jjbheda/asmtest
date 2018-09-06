package com.example.jiangjingbo.sec;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.jiangjingbo.asmtest.R;
import com.qiyi.loglibrary.LogManager;
import com.qiyi.loglibrary.LogStorer;
import com.qiyi.loglibrary.upload.LogUpload;

public class SecActivity extends AppCompatActivity {

    public static final String TAG = "SecActivity";
    Button btn_sec, btn_upload;
    int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sec);
        btn_sec = findViewById(R.id.btn_sec);
        btn_upload = findViewById(R.id.btn_upload);
        btn_sec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
//                    int[] array = {1, 2, 3, 4, 5};
                    for (int i = 0; i < 10000; i++) {
                        LogStorer.w("FW", i + "");
                    }
                } catch (OutOfMemoryError e) {

                } catch (ArrayIndexOutOfBoundsException e) {
                    LogStorer.w("FW", e);
                } catch (IndexOutOfBoundsException e) {

                } catch (Exception e) {

                }
            }
        });

        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUpload.getLog("FW", "2018-09-06");
            }
        });

    }

}
