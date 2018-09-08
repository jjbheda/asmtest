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
                new Thread(new Runnable() {
                    @Override
                    public void run() {
//                        for (int i = 0; i< 1000; i++) {
                        tesForThrowable();
//                        }
                    }
                }).start();
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

        int[] array = {1, 2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,29,20,21
        ,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40};

        for (int i = 0; i< 40; i++) {
                try {
                    LogStorer.w("FW"+ i , array[i] + "");

                    for (int j = 0; j < 10000; j++) {


                        LogStorer.w("FW"+ i , array[i] + "");
                    }
                } catch (OutOfMemoryError e) {

                } catch (ArrayIndexOutOfBoundsException e) {
                    LogStorer.w("FW", e);
                } catch (IndexOutOfBoundsException e) {

                } catch (Exception e) {

            }
        }
    }

    private void tesForThrowable() {

        int[] array = {1, 2,3,4,5,6,7,8,9,10};

        for (int j = 0; j < 10000; j++) {
            for (int i = 0; i < 11; i++) {
                try {
                    LogStorer.w("FW"+ i , j + "");

                } catch (OutOfMemoryError e) {

                } catch (ArrayIndexOutOfBoundsException e) {

                } catch (IndexOutOfBoundsException e) {

                } catch (Exception e) {

                }
            }
        }


    }
}
