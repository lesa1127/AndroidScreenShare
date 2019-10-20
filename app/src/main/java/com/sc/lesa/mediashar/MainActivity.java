package com.sc.lesa.mediashar;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    AlertDialog.Builder inputDialog;
    AlertDialog alertInputDialog;
    View dialogView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_activity_main);

        if(Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();

            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);

            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        dialogView = LayoutInflater.from(MainActivity.this)
                .inflate(R.layout.dialo_inputadreass,null);

        buildDialog();

        if (Build.VERSION.SDK_INT>Build.VERSION_CODES.M) {
            CheckAndSetPermissions(this);
        }
    }

    private void buildDialog(){
        inputDialog = new AlertDialog.Builder(MainActivity.this);
        inputDialog.setTitle(getString(R.string.app_input_address));
        inputDialog.setView(dialogView);
        inputDialog.setNegativeButton(getText(R.string.app_but_ok),new  DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText edit_text =
                        (EditText) dialogView.findViewById(R.id.editText_ip);

                Intent intent = new Intent(MainActivity.this,WatchContect.class);
                intent =  WatchContect.buildIntent(intent,edit_text.getText().toString());
                MainActivity.this.startActivity(intent);

            }
        } );
        inputDialog.setNeutralButton(getText(R.string.app_but_cancle),new  DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertInputDialog = inputDialog.create();

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.but_main_share:
                Intent intent =new Intent(this,MediaProjectionActivity.class);
                startActivity(intent);
                break;
            case R.id.but_main_seach:
                alertInputDialog.show();
                break;
        }
    }

    public static void CheckAndSetPermissions(Activity activity){
        final int REQUEST = 1;
        String[] PERMISSIONS = {
                "android.permission.READ_EXTERNAL_STORAGE",
                "android.permission.WRITE_EXTERNAL_STORAGE",
                "android.permission.FOREGROUND_SERVICE",
                Manifest.permission.RECORD_AUDIO
        };
        try {
            for (String ps :PERMISSIONS) {
                //检测是否有写的权限
                int permission = ActivityCompat.checkSelfPermission(activity, ps);
                if (permission != PackageManager.PERMISSION_GRANTED) {
                    // 没有写的权限，去申请写的权限，会弹出对话框
                    ActivityCompat.requestPermissions(activity, PERMISSIONS, REQUEST);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
