package com.sc.lesa.mediashar

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.databinding.BaseObservable
import android.databinding.Bindable
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.DisplayMetrics
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import com.sc.lesa.mediashar.databinding.StratServerActivityBinding
import java.util.*

class MediaProjectionActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var myApplication: MyApplication
    lateinit var mediaProjectionManager: MediaProjectionManager
    var preTime: Long = 0
    lateinit var binding:StratServerActivityBinding

    companion object {
        const val REQUEST_MEDIA_PROJECTION = 18
    }

    val viewmodel=ViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.strat_server_activity)
        binding.model=viewmodel
        myApplication=application as MyApplication
    }



//
//    private fun init() {
//
//        val metrics = DisplayMetrics()
//        windowManager.defaultDisplay.getRealMetrics(metrics)
//        val width = metrics.widthPixels
//        val height = metrics.heightPixels
//        val config: Config = Config.getConfig(this)
//        config.setWidth(width)
//        config.setHeight(height)
//        Config.Companion.saveConfig(this, config)
//        myServiceConnection = MyServiceConnection(this)
//    }

    override fun onResume() {
        super.onResume()

        if (Build.VERSION.SDK_INT >= 21) {
            val decorView = window.decorView
            //让应用主题内容占用系统状态栏的空间,注意:下面两个参数必须一起使用 stable 牢固的
            val option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            decorView.systemUiVisibility = option
            //设置状态栏颜色为透明
            window.statusBarColor = Color.TRANSPARENT
        }
        //隐藏标题栏
        val actionBar = supportActionBar
        actionBar!!.hide()

        if (myApplication.serverStatus==MediaReaderService.ServerStatus.UNSTART){
            viewmodel.step=ModelStatus.UNSTART
        }else{
            viewmodel.step=ModelStatus.STARTED
        }
    }

    override fun onClick(v: View) {
        if (v.id == R.id.but_server_start) {
            if (viewmodel.step==ModelStatus.UNSTART){
                viewmodel.step=ModelStatus.STARTING
                requestCapturePermission()
            }else if (viewmodel.step==ModelStatus.STARTED){
                //stop
                stopServer()
                viewmodel.step=ModelStatus.UNSTART
                myApplication.serverStatus=MediaReaderService.ServerStatus.UNSTART
            }

        } else if (v.id == R.id.but_server_setting) {
            val intent = Intent(this, SettingParam::class.java)
            startActivity(intent)
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            val currentTime = Date().time
            if (currentTime - preTime > 2000) {
                Toast.makeText(this, getText(R.string.app_back_exit), Toast.LENGTH_SHORT).show()
                preTime = currentTime
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }


    private fun requestCapturePermission() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            //5.0 之后才允许使用屏幕截图
            mediaProjectionManager = getSystemService(
                    Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
            startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(),
                    REQUEST_MEDIA_PROJECTION)
        } else {
            Toast.makeText(this, "系统版本低于5.0!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_MEDIA_PROJECTION -> if (resultCode == Activity.RESULT_OK && data != null) {
                val mediaProjection = mediaProjectionManager.getMediaProjection(resultCode, data)
                if (mediaProjection == null) {
                    Log.e(this.javaClass.name, "media projection is null")
                    return
                }
                myApplication.mediaProjection = mediaProjection
                startServer()
                viewmodel.step=ModelStatus.STARTED
                myApplication.serverStatus=MediaReaderService.ServerStatus.STARTED
            } else {
                viewmodel.step=ModelStatus.UNSTART
            }
        }
    }

    fun startServer() {
        val intent=Intent(this,MediaReaderService::class.java)
        intent.putExtra("CMD",1)
        startService(intent)
    }

    fun stopServer(){
        val intent=Intent(this,MediaReaderService::class.java)
        intent.putExtra("CMD",2)
        startService(intent)
    }



    inner class ViewModel:BaseObservable(){

        var step=ModelStatus.UNSTART
        set(value) {
            when(value){
                ModelStatus.UNSTART->{
                    buttontext=getString(R.string.app_but_share)
                    buttonenable=true
                }
                ModelStatus.STARTING->{
                    buttontext=getString(R.string.app_but_share)
                    buttonenable=false
                }
                ModelStatus.STARTED->{
                    buttontext=getString(R.string.app_but_stop)
                    buttonenable=true
                }
            }
            field=value
        }

        @Bindable
        var buttontext=""
        set(value) {
            field=value
            notifyChange()
        }

        @Bindable
        var buttonenable=true
        set(value) {
            field=value
            notifyChange()
        }

    }

    enum class ModelStatus{
        UNSTART,
        STARTING,
        STARTED
    }

}