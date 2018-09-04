package com.example.jiangjingbo.asmtest;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.jiangjingbo.sec.SecActivity;
import com.qiyi.loglibrary.LogEntity;
import com.qiyi.loglibrary.LogStorer;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    Button btn,btn_02;
    private boolean hasPermission;
    private static final int PERMISSIONS_REQUEST_EXTERNAL_STORAGE = 1;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_main);
        btn = (Button) findViewById(R.id.btn);
        btn_02 = (Button) findViewById(R.id.btn_02);


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int[] ss = new int[]{1, 2, 3, 4, 5 ,6 ,7 ,8 ,9 ,10 ,11 ,12 ,13};
                    int c = 0;
                    for (int i = 0; i < 20; i++) {
                      c = ss[i];
                    }

                } catch (OutOfMemoryError e) {

                } catch (ArrayIndexOutOfBoundsException e) {
                    LogStorer.w("BI", "this is a error", e);

                } catch (IndexOutOfBoundsException e) {
                    LogStorer.w("this is a error", e);
                } catch (Exception e) {
                    LogStorer.w("this is a error", e);
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


        // Check permission.
        hasPermission = hasPermission();
        if (!hasPermission) {
            if (shouldShowRequestPermissionRationale()) {
                showPermissionRequestDialog(false);
            } else {
                requestPermission();
            }
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN,priority = 100)
    public void onMessageEvent(LogEntity event) {/* Do something */};


    private boolean hasPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }

    private boolean shouldShowRequestPermissionRationale() {
        return ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                PERMISSIONS_REQUEST_EXTERNAL_STORAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                hasPermission = grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (!hasPermission) {
                    if (shouldShowRequestPermissionRationale()) {
                        showPermissionRequestDialog(false);
                    } else {
                        showPermissionRequestDialog(true);
                    }
                }
            }
        }
    }

    /**
     * Show a dialog for user to explain about the permission.
     */
    private void showPermissionRequestDialog(final boolean gotoSettings) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.permission_request)
                .setMessage(R.string.permission_explanation)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(gotoSettings ? R.string.go_to_settings : R.string.allow,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (gotoSettings) {
                                    startAppSettings();
                                } else {
                                    requestPermission();
                                }
                            }
                        })
                .show();
    }

    private void startAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }

}
