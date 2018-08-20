package com.example.jiangjingbo.asmtest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.jiangjingbo.sec.SecActivity;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    Button btn,btn_02;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn = (Button) findViewById(R.id.btn);
        btn_02 = (Button) findViewById(R.id.btn_02);

        btn.setOnClickListener(new View.OnClickListener() {
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

        btn_02.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SecActivity.class);
                startActivity(intent);
            }
        });

    }

    @BindGet("mike")
    String getHttp(String param) {
        String url = "http://www.baidu.com/?username" + param;
        return url;
    }

}
