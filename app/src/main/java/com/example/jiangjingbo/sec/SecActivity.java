package com.example.jiangjingbo.sec;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.jiangjingbo.asmtest.BindGet;
import com.example.jiangjingbo.asmtest.R;

public class SecActivity extends AppCompatActivity {

    public static final String TAG = "SecActivity";
    Button btn_sec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sec);
        btn_sec = (Button) findViewById(R.id.btn_sec);

        btn_sec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int[] ss = new int[]{1, 2, 3, 4, 5};
                    for (int i = 0; i < 7; i++) {
                        Log.e(TAG, ss[i] + "");
                    }

                } catch (OutOfMemoryError e) {

                } catch (ArrayIndexOutOfBoundsException e) {

                } catch (IndexOutOfBoundsException e) {

                } catch (Exception e) {

                }
            }
        });

    }

    @BindGet("mike")
    String getHttp(String param) {
        String url = "http://www.baidu.com/?username" + param;
        return url;
    }

}