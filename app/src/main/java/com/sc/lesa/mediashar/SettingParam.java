package com.sc.lesa.mediashar;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioFormat;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.sc.lesa.mediashar.jlib.P.VideoSender;
import com.sc.lesa.mediashar.jlib.P.VoiceSender;
import com.sc.lesa.mediashar.jlib.media.AacFormat;

public class SettingParam extends AppCompatActivity implements View.OnClickListener{
    private int width;
    private int height;
    private int videoBitrate;
    private int videoFrameRate;
    private int ChannelMode,EncodeFormat,ChannelCount,ByteRate, SampleRate;

    EditText editText_Width,editText_Height,editText_VideoBitrate,editText_VideoFrameRate,
    editText_ChannelCount,editText_ByteRate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_param);

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        initview();
        loadData();
    }

    private void initview(){
        editText_Width =(EditText) findViewById(R.id.setting_editText);
        editText_Height=(EditText) findViewById(R.id.setting_editText3);
        editText_VideoBitrate=(EditText) findViewById(R.id.setting_editText4);
        editText_VideoFrameRate=(EditText) findViewById(R.id.setting_editText5);
        editText_ChannelCount=(EditText) findViewById(R.id.setting_editText6);
        editText_ByteRate=(EditText) findViewById(R.id.setting_editText7);
    }

    private void loadData(){
        SharedPreferences sharedPreferences = getSharedPreferences(
                "videoparm",Context.MODE_PRIVATE);
        this.width=sharedPreferences.getInt("width",1920);
        this.height=sharedPreferences.getInt("height",1080);
        this.videoBitrate=sharedPreferences.getInt("videoBitrate",16777216);
        this.videoFrameRate=sharedPreferences.getInt("videoFrameRate",24);

        sharedPreferences = getSharedPreferences(
                "voiceparam",Context.MODE_PRIVATE);

        this.ChannelMode=sharedPreferences.getInt("ChannelMode",AudioFormat.CHANNEL_IN_MONO);
        this.EncodeFormat=sharedPreferences.getInt("EncodeFormat",AudioFormat.ENCODING_PCM_16BIT);
        this.ChannelCount=sharedPreferences.getInt("ChannelCount",1);
        this.ByteRate=sharedPreferences.getInt("ByteRate",AacFormat.ByteRate384Kbs);
        this.SampleRate=sharedPreferences.getInt("SampleRate",AacFormat.SampleRate44100);

        editText_Width.setText(""+width);
        editText_Height.setText(""+height);
        editText_VideoBitrate.setText(""+videoBitrate);
        editText_VideoFrameRate.setText(""+videoFrameRate);
        editText_ChannelCount.setText(""+ChannelCount);
        editText_ByteRate.setText(""+ByteRate);
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.setting_button){
            width=Integer.parseInt(editText_Width.getText().toString());
            height=Integer.parseInt(editText_Height.getText().toString());
            videoBitrate=Integer.parseInt(editText_VideoBitrate.getText().toString());
            videoFrameRate=Integer.parseInt(editText_VideoFrameRate.getText().toString());
            ChannelCount=Integer.parseInt(editText_ChannelCount.getText().toString());
            ByteRate=Integer.parseInt(editText_ByteRate.getText().toString());
            ChannelMode = ChannelCount==1?AudioFormat.CHANNEL_IN_MONO:AudioFormat.CHANNEL_IN_STEREO;
            EncodeFormat = AudioFormat.ENCODING_PCM_16BIT;
            SampleRate = AacFormat.SampleRate44100;
            VideoSender.setParam(this,width,height,videoBitrate,videoFrameRate);
            VoiceSender.setParam(this,ChannelMode,EncodeFormat,ChannelCount,ByteRate,SampleRate);
        }
    }
}
