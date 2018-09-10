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
    Button btn_sec, btn_tr, btn_upload;
    int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sec);
        btn_sec = findViewById(R.id.btn_sec);
        btn_upload = findViewById(R.id.btn_upload);
        btn_tr = findViewById(R.id.btn_tr);
        btn_sec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        tesForCommonLog();
                    }
                }).start();
            }
        });

        btn_tr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                       tesForMsg();
                    }
                },"Passport").start();

            }
        });

        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUpload.getLog("log_0", "2018-09-06");
            }
        });
    }

    private void tesForMsg() {

        int[] array = {1, 2};

        for (int i = 0; i< 100; i++) {
                try {
//                    LogStorer.w("Passport"+ i , array[i] + "");
                    for (int j = 0; j < 3; j++) {
                        Log.w("Passport", array[i] + "");
                    }
                } catch (OutOfMemoryError e) {

                } catch (ArrayIndexOutOfBoundsException e) {
//                    LogStorer.w("Passport", e);
                } catch (IndexOutOfBoundsException e) {

                } catch (Exception e) {

            }
        }
    }

    private void tesForCommonLog() {

        for (int j = 0; j < 10000; j++) {
            for (int i = 0; i < 11; i++) {
                try {
                    Log.w("FW"+ i , j + "");
                } catch (OutOfMemoryError e) {

                } catch (ArrayIndexOutOfBoundsException e) {

                } catch (IndexOutOfBoundsException e) {

                } catch (Exception e) {

                }
            }
        }


    }
}
