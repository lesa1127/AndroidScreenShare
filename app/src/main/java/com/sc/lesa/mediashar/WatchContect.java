package com.sc.lesa.mediashar;



import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;


import com.sc.lesa.mediashar.jlib.P.VideoPlayThread;
import com.sc.lesa.mediashar.jlib.P.VoicePlayThread;
import com.sc.lesa.mediashar.jlib.server.SocketClientThread;

import java.io.IOException;
import java.util.Date;


public class WatchContect extends AppCompatActivity {

    private SurfaceView mSurfaceView;
    SurfaceHolder mSurfaceHolder;

    String ip;
    SocketClientThread socketClientThread;
    VideoPlayThread mdiaPlayThread;
    VoicePlayThread voicePlayThread;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1){
                init();
            }
            else if (msg.what==2){
                socketClientThread.exit();
                if(mdiaPlayThread!=null)mdiaPlayThread.exit();
                if (voicePlayThread!=null)voicePlayThread.exit();
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_contect);

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        mSurfaceView = (SurfaceView)findViewById(R.id.surfaceView_watch);
        mSurfaceHolder = mSurfaceView.getHolder();

        Intent intent = getIntent();
        ip=intent.getStringExtra("Address");

        mSurfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                Message msg = new Message();
                msg.what = 1;
                handler.sendMessage(msg);
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

            }
        });
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
            Message msg = new Message();
            msg.what = 2;
            handler.sendMessage(msg);
            this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }


    private void init(){
        Thread thread =new Thread(){
            @Override
            public void run() {
                socketClientThread=new SocketClientThread(ip,9090);
                try {
                    socketClientThread.connect();
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
                socketClientThread.start();
                mdiaPlayThread=new VideoPlayThread(mSurfaceHolder.getSurface(),socketClientThread.getDataPackList());
                mdiaPlayThread.start();
                voicePlayThread = new VoicePlayThread(socketClientThread.getDataPackList());
                voicePlayThread.start();
            }
        };
        thread.start();
    }

    public static Intent buildIntent(Intent intent,String ip){
        intent.putExtra("Address",ip);
        return intent;
    }


}
