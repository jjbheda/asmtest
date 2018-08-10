package com.example.jiangjingbo.asmtest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.printer.LogPrinter;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    String address;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.e(TAG,getHttp("zhangsan") + "");

        try {
           int[] ss = new int[]{1,2,3,4,5};
           for (int i=0;i<7;i++) {
               Log.e(TAG,ss[i]+"");
           }

        } catch (Exception e) {

        }
     }

    @BindGet("mike")
    String getHttp(String param){
        String url="http://www.baidu.com/?username"+param;
        return url;
    }

}
