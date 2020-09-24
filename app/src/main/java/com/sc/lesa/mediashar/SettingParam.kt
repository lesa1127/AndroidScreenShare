package com.sc.lesa.mediashar

import android.databinding.DataBindingUtil
import android.media.AudioFormat
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.sc.lesa.mediashar.config.Config
import com.sc.lesa.mediashar.databinding.ActivitySettingParamBinding

class SettingParam : AppCompatActivity(), View.OnClickListener {
    lateinit var config:Config
    lateinit var binding:ActivitySettingParamBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_setting_param)
        config= Config.getConfig(this)
        binding.config=config
    }

    override fun onResume() {
        super.onResume()
        val decorView = window.decorView
        decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        val actionBar = supportActionBar
        actionBar!!.hide()
    }



    override fun onClick(v: View) {
        if (v.id == R.id.setting_button) {
            try {
                config.width.toInt()
                config.height.toInt()
                config.videoBitrate.toInt()
                config.videoFrameRate.toInt()


                if (config.channelCount.toInt()==1){
                    config.channelMode=AudioFormat.CHANNEL_IN_MONO
                }else if (config.channelCount.toInt()==2){
                    config.channelMode=AudioFormat.CHANNEL_IN_STEREO
                }else{
                    throw Exception(getString(R.string.error_channel_count))
                }
                config.voiceByteRate.toInt()
                config.save(this)
            }catch (t:Throwable){
                t.printStackTrace()
                Toast.makeText(this,"${getString(R.string.error_save)}:${t.message}",Toast.LENGTH_SHORT).show()
            }

        }
    }
}