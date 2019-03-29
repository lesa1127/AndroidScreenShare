package com.sc.lesa.mediashar;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;


public class MediaProjectionActivity extends AppCompatActivity implements View.OnClickListener {

    private MediaProjection mediaProjection=null;
    private  int width,heght;
    MyServiceConnection myServiceConnection;

    Button buttonstart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.strat_server_activity);
        init();


    }

    @Override
    protected void onResume() {
        Intent intent = new Intent(this,MediaReaderService.class);
        bindService(intent,myServiceConnection,Context.BIND_AUTO_CREATE);
        super.onResume();
    }

    @Override
    protected void onPause() {
        try {
            unbindService(myServiceConnection);
        }catch (Throwable e){
            e.printStackTrace();
        }
        super.onPause();
    }
    @Override
    protected void onRestart() {
        Intent intent = new Intent(this,MediaReaderService.class);
        bindService(intent,myServiceConnection,Context.BIND_AUTO_CREATE);
        super.onRestart();
    }
    @Override
    protected void onStop() {
        try {
            unbindService(myServiceConnection);
        }catch (Throwable e){
            e.printStackTrace();
        }
        super.onStop();
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.but_server_start){
            Button button = (Button) v;
            if (button.getText().toString().equals(getString(R.string.app_but_shar))) {
                buttonstart.setEnabled(false);
                try {
                    requestCapturePermission();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            else {
                myServiceConnection.closeServer();
                button.setText(R.string.app_but_shar);
                try {
                    unbindService(myServiceConnection);
                }catch (Throwable e){
                    e.printStackTrace();
                }
                stopService(new Intent(this,MediaReaderService.class));
            }


        }
        else if (v.getId()== R.id.but_server_setting){
            Intent intent =new Intent(this,SettingParam.class);
            startActivity(intent);
        }

    }




    private void init() {
        if(Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            //让应用主题内容占用系统状态栏的空间,注意:下面两个参数必须一起使用 stable 牢固的
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            //设置状态栏颜色为透明
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        //隐藏标题栏
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        buttonstart = (Button)findViewById(R.id.but_server_start);
        buttonstart.setEnabled(false);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        this.width = width;
        this.heght = height;

        myServiceConnection = new MyServiceConnection(this);
        Intent intent = new Intent(this,MediaReaderService.class);
        bindService(intent,myServiceConnection,Context.BIND_AUTO_CREATE);
    }

    public static final int REQUEST_MEDIA_PROJECTION = 18;
    MediaProjectionManager mediaProjectionManager;
    private void requestCapturePermission() throws Exception{

        if ((Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP)) {
            //5.0 之后才允许使用屏幕截图
            mediaProjectionManager = (MediaProjectionManager) getSystemService(
                    Context.MEDIA_PROJECTION_SERVICE);
            startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(),
                    REQUEST_MEDIA_PROJECTION);
        }else {
            throw new Exception("android版本低于5.0");
        }
        return;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_MEDIA_PROJECTION:

                if (resultCode == RESULT_OK && data != null) {
                    MediaProjection mediaProjection = mediaProjectionManager.getMediaProjection(resultCode, data);
                    if (mediaProjection == null) {
                        Log.e("@@", "media projection is null");
                        return;
                    }
                    this.mediaProjection=mediaProjection;
                    onResultCode();

                }else {
                    buttonstart.setText(R.string.app_but_shar);
                    buttonstart.setEnabled(true);
                }
                break;
        }

    }

    public void  onResultCode(){

        Intent intent = new Intent(this,MediaReaderService.class);
        MediaReaderService.startServer(intent,this.mediaProjection);
        startService(intent);

        buttonstart.setText(R.string.app_but_stop);
        buttonstart.setEnabled(true);

    }

    private class MyServiceConnection implements ServiceConnection{

        MediaProjectionActivity mediaProjectionActivity;
        MediaReaderService.MyBinder myBinder;

        public MyServiceConnection(MediaProjectionActivity context){
            this.mediaProjectionActivity=context;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            myBinder=(MediaReaderService.MyBinder) service;

            SharedPreferences sharedPreferences = mediaProjectionActivity.getSharedPreferences(
                    "videoparm",Context.MODE_PRIVATE);
            int localwidth=sharedPreferences.getInt("width",mediaProjectionActivity.width);
            int localheight=sharedPreferences.getInt("height",mediaProjectionActivity.heght);
            int videoBitrate=sharedPreferences.getInt("videoBitrate",16777216);
            int videoFrameRate=sharedPreferences.getInt("videoFrameRate",24);

            myBinder.setVideoParam(localwidth,localheight,
                    videoBitrate,videoFrameRate);

            if (myBinder.getServerStatus()) {
                mediaProjectionActivity.buttonstart.setText(R.string.app_but_stop);
            }else {
                mediaProjectionActivity.buttonstart.setText(R.string.app_but_shar);
            }
            mediaProjectionActivity.buttonstart.setEnabled(true);


        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }

        @Override
        public void onBindingDied(ComponentName name) {

        }

        @Override
        public void onNullBinding(ComponentName name) {

        }

        public void closeServer(){
            myBinder.stopServer();
        }

        public boolean getServerStatus(){
            return myBinder.getServerStatus();
        }

    }

}
