package com.sc.lesa.mediashar;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
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
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;


public class MediaProjectionActivity extends AppCompatActivity implements View.OnClickListener {

    MyServiceConnection myServiceConnection;

    Button buttonstart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.strat_server_activity);
        init();
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

        Config config = Config.Companion.getConfig(this);
        config.setWidth(width);
        config.setHeight(height);
        Config.Companion.saveConfig(this,config);

        myServiceConnection = new MyServiceConnection(this);
        Intent intent = new Intent(this,MediaReaderService.class);
        bindService(intent,myServiceConnection,Context.BIND_AUTO_CREATE);
        myServiceConnection.reloadUI();
    }

    @Override
    protected void onResume() {
        myServiceConnection.reloadUI();
        super.onResume();
    }

    @Override
    protected void onRestart() {
        myServiceConnection.reloadUI();
        super.onRestart();
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
                myServiceConnection.stopShare();
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

    long preTime;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            long currentTime = new Date().getTime();
            if ((currentTime - preTime) > 2000) {
                Toast.makeText(this, getText(R.string.app_back_exit), Toast.LENGTH_SHORT).show();
                preTime = currentTime;
                return true;
            }

            myServiceConnection.unBind();
        }
        return super.onKeyDown(keyCode, event);
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
                    MyApplication.setMediaProjection(mediaProjection);
                    onResultCode();

                    buttonstart.setText(R.string.app_but_stop);
                    buttonstart.setEnabled(true);
                }else {
                    buttonstart.setText(R.string.app_but_shar);
                    buttonstart.setEnabled(true);
                }
                break;
        }

    }

    public void  onResultCode(){
        myServiceConnection.startShare();
    }

    private class MyServiceConnection implements ServiceConnection{
        boolean isConnect=false;
        MediaProjectionActivity mediaProjectionActivity;
        MediaReaderService.MyBinder myBinder;
        ArrayList<Runnable> task = new ArrayList<Runnable>();

        public MyServiceConnection(MediaProjectionActivity context){
            this.mediaProjectionActivity=context;
        }

        void reloadUI(){
            if (isConnect){
                if (myBinder.getServerStatus()==MediaReaderService.START_SERVER) {
                    mediaProjectionActivity.buttonstart.setText(R.string.app_but_stop);
                }else {
                    mediaProjectionActivity.buttonstart.setText(R.string.app_but_shar);
                }
                mediaProjectionActivity.buttonstart.setEnabled(true);
            }else {
                reConnect();
                task.add(()->{
                    reloadUI();
                });
            }

        }
        void stopShare(){
            if (isConnect){
                myBinder.stopShare();
            }else {
                reConnect();
                task.add(()->{
                    stopShare();
                });
            }
        }

        void startShare(){
            if (isConnect){
                myBinder.startShare();
            }else {
                reConnect();
                task.add(()->{
                    startShare();
                });
            }
        }

        void reConnect(){
            Intent intent = new Intent(MediaProjectionActivity.this,MediaReaderService.class);
            mediaProjectionActivity.bindService(intent,this,Context.BIND_AUTO_CREATE);
        }

        void unBind(){
            if (isConnect)mediaProjectionActivity.unbindService(this);
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            myBinder=(MediaReaderService.MyBinder) service;
            isConnect=true;

            for (Runnable i:task){
                i.run();
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isConnect=false;
        }

        @Override
        public void onBindingDied(ComponentName name) {

        }

        @Override
        public void onNullBinding(ComponentName name) {

        }
    }

}
