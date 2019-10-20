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

    private Config config;
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
        config = Config.Companion.getConfig(this);

        editText_Width.setText(""+config.getWidth());
        editText_Height.setText(""+config.getWidth());
        editText_VideoBitrate.setText(""+config.getVideoBitrate());
        editText_VideoFrameRate.setText(""+config.getVideoFrameRate());
        editText_ChannelCount.setText(""+config.getChannelCount());
        editText_ByteRate.setText(""+config.getVoiceByteRate());
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.setting_button){
            config.setWidth(Integer.parseInt(editText_Width.getText().toString()));
            config.setHeight(Integer.parseInt(editText_Height.getText().toString()));
            config.setVideoBitrate(Integer.parseInt(editText_VideoBitrate.getText().toString()));
            config.setVideoFrameRate(Integer.parseInt(editText_VideoFrameRate.getText().toString()));
            config.setChannelCount(Integer.parseInt(editText_ChannelCount.getText().toString()));
            config.setVoiceByteRate(Integer.parseInt(editText_ByteRate.getText().toString()));
            config.setChannelMode(config.getChannelCount()==1?AudioFormat.CHANNEL_IN_MONO:AudioFormat.CHANNEL_IN_STEREO);


            VideoSender.setParam(this,
                    config.getWidth(),config.getHeight(),
                    config.getVideoBitrate(),config.getVideoFrameRate());

            VoiceSender.setParam(this,
                    config.getChannelMode(),config.getEncodeFormat(),
                    config.getChannelCount(),config.getVoiceByteRate(),
                    config.getVoiceSampleRate());
        }
    }
}
