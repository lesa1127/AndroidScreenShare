package com.sc.lesa.mediashar

import android.content.Context
import android.media.AudioFormat
import com.sc.lesa.mediashar.jlib.media.AacFormat

class Config(var width:Int,
             var height:Int,
             var videoBitrate:Int,
             var videoFrameRate:Int,

             var channelMode:Int,
             var encodeFormat:Int,
             var channelCount:Int,
             var voiceByteRate:Int,
             var voiceSampleRate:Int
             ) {


    companion object {
        private val CONFIGNAME = "configfile"

        private val WIDTH = "Width"
        private val HEIGHT = "Height"
        private val VIDEOBITRATE = "VideoBitrate"
        private val VIDEOFRAMERATE = "VideoFrameRate"

        private val CHANNELMODE="ChannelMode"
        private val ENCODEFORMAT="EncodeFormat"
        private val CHANNELCOUNT = "ChannelCount"
        private val VOICEBYTERATE = "VoiceByteRate"
        private val VOICESAMPLERATE = "VoiceSampleRate"


        private var config: Config? = null

        fun getConfig(context: Context): Config {
            if (config == null) {
                val sp = context.getSharedPreferences(CONFIGNAME, Context.MODE_PRIVATE)
                config = Config(
                        sp.getInt(WIDTH, 1080),
                        sp.getInt(HEIGHT, 1920),
                        sp.getInt(VIDEOBITRATE, 16777216),
                        sp.getInt(VIDEOFRAMERATE, 24),

                        sp.getInt(CHANNELMODE, AudioFormat.CHANNEL_IN_MONO),
                        sp.getInt(ENCODEFORMAT,AudioFormat.ENCODING_PCM_16BIT),
                        sp.getInt(CHANNELCOUNT, 1),
                        sp.getInt(VOICEBYTERATE, AacFormat.ByteRate384Kbs),
                        sp.getInt(VOICESAMPLERATE,AacFormat.SampleRate44100)
                )
            }
            return config as Config

        }

        fun saveConfig(context: Context,config: Config){
            Config.config=config

            val sp = context.getSharedPreferences(CONFIGNAME, Context.MODE_PRIVATE)
            val edit = sp.edit()

            edit.putInt(WIDTH,config.width)
            edit.putInt(HEIGHT, config.height)
            edit.putInt(VIDEOBITRATE, config.videoBitrate)
            edit.putInt(VIDEOFRAMERATE,config.videoFrameRate)

            edit.putInt(CHANNELMODE,config.channelMode)
            edit.putInt(ENCODEFORMAT,config.encodeFormat)
            edit.putInt(CHANNELCOUNT,config.channelCount)
            edit.putInt(VOICEBYTERATE,config.voiceByteRate)
            edit.putInt(VOICESAMPLERATE,config.voiceSampleRate)

            edit.apply()
        }



    }
}