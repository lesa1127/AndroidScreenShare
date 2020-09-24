
package com.sc.lesa.mediashar.config

import android.content.Context
import android.databinding.BaseObservable
import android.databinding.Bindable
import com.sc.lesa.mediashar.BR
class Config(): BaseObservable() {
    val filename = "Configfile"

    companion object{
        private var value: Config?=null
        fun getConfig(context: Context):Config{
            if (value==null){
                synchronized(Config::class.java) {
                    if (value==null){
                        value=Config()
                        value!!.init(context)
                    }
                }
            }
            return value!!
        }
    }

    fun init(context:Context){
        val sp = context.getSharedPreferences(filename,Context.MODE_PRIVATE)
        width=sp.getString("width",width)
        height=sp.getString("height",height)
        videoBitrate=sp.getString("videoBitrate",videoBitrate)
        videoFrameRate=sp.getString("videoFrameRate",videoFrameRate)
        channelCount=sp.getString("channelCount",channelCount)
        voiceByteRate=sp.getString("voiceByteRate",voiceByteRate)
        voiceSampleRate=sp.getString("voiceSampleRate",voiceSampleRate)
        channelMode=sp.getInt("channelMode",channelMode)
        encodeFormat=sp.getInt("encodeFormat",encodeFormat)

    }
    
    fun save(context:Context){
        context.getSharedPreferences(filename,Context.MODE_PRIVATE).also {sp->
            sp.edit().also {
                it.putString("width",width)
                it.putString("height",height)
                it.putString("videoBitrate",videoBitrate)
                it.putString("videoFrameRate",videoFrameRate)
                it.putString("channelCount",channelCount)
                it.putString("voiceByteRate",voiceByteRate)
                it.putString("voiceSampleRate",voiceSampleRate)
                it.putInt("channelMode",channelMode)
                it.putInt("encodeFormat",encodeFormat)

                it.apply()
            }
        }
    }
    
    
    @Bindable
    var width:String="1080"
    set(value) {
        field=value
        notifyPropertyChanged(BR.width)
    }
            
    @Bindable
    var height:String="1920"
    set(value) {
        field=value
        notifyPropertyChanged(BR.height)
    }
            
    @Bindable
    var videoBitrate:String="16777216"
    set(value) {
        field=value
        notifyPropertyChanged(BR.videoBitrate)
    }
            
    @Bindable
    var videoFrameRate:String="24"
    set(value) {
        field=value
        notifyPropertyChanged(BR.videoFrameRate)
    }
            
    @Bindable
    var channelCount:String="1"
    set(value) {
        field=value
        notifyPropertyChanged(BR.channelCount)
    }
            
    @Bindable
    var voiceByteRate:String="384000"
    set(value) {
        field=value
        notifyPropertyChanged(BR.voiceByteRate)
    }
            
    @Bindable
    var voiceSampleRate:String="44100"
    set(value) {
        field=value
        notifyPropertyChanged(BR.voiceSampleRate)
    }
            
    @Bindable
    var channelMode:Int=16
    set(value) {
        field=value
        notifyPropertyChanged(BR.channelMode)
    }
            
    @Bindable
    var encodeFormat:Int=2
    set(value) {
        field=value
        notifyPropertyChanged(BR.encodeFormat)
    }
            
}
    