package com.sc.lesa.mediashar

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.databinding.BaseObservable
import android.databinding.Bindable
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.DialogFragment
import android.support.v7.app.AppCompatActivity
import android.view.*
import com.sc.lesa.mediashar.databinding.DialoInputadreassBinding


class MainActivity : AppCompatActivity(), View.OnClickListener {

    val mInputModel=InputModel()
    lateinit var inputDialog: InputDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.first_activity_main)

        inputDialog=InputDialog(this)
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            CheckAndSetPermissions(this)
        }
    }


    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT >= 21) {
            val decorView = window.decorView
            val option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            decorView.systemUiVisibility = option
            window.statusBarColor = Color.TRANSPARENT
        }
        val actionBar = supportActionBar
        actionBar!!.hide()
    }



    override fun onClick(v: View) {
        val id = v.id
        when (id) {
            R.id.but_main_share -> {
                val intent = Intent(this, MediaProjectionActivity::class.java)
                startActivity(intent)
            }
            R.id.but_main_seach ->{
                inputDialog.show(supportFragmentManager,this.javaClass.name)
            }
        }
    }

    companion object {
        fun CheckAndSetPermissions(activity: Activity?) {
            val REQUEST = 1
            val PERMISSIONS = arrayOf(
                    "android.permission.READ_EXTERNAL_STORAGE",
                    "android.permission.WRITE_EXTERNAL_STORAGE",
                    "android.permission.FOREGROUND_SERVICE",
                    Manifest.permission.RECORD_AUDIO
            )
            for (ps in PERMISSIONS) { //检测是否有写的权限
                val permission = ActivityCompat.checkSelfPermission(activity!!, ps)
                if (permission != PackageManager.PERMISSION_GRANTED) { // 没有写的权限，去申请写的权限，会弹出对话框
                    ActivityCompat.requestPermissions(activity, PERMISSIONS, REQUEST)
                }
            }
        }
    }


    @SuppressLint("ValidFragment")
    class  InputDialog(val mainactivity:MainActivity): DialogFragment() {
        lateinit var binding:DialoInputadreassBinding


        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            binding=DataBindingUtil.inflate(inflater,R.layout.dialo_inputadreass,null,false)
            binding.model=mainactivity.mInputModel
            binding.callback=this
            return binding.root
        }

        override fun onStart() {
            super.onStart()
            val window = dialog.window!!
            val params: WindowManager.LayoutParams = window.getAttributes()
            params.gravity = Gravity.CENTER
            // 使用ViewGroup.LayoutParams，以便Dialog 宽度充满整个屏幕
            // 使用ViewGroup.LayoutParams，以便Dialog 宽度充满整个屏幕
            params.width = ViewGroup.LayoutParams.MATCH_PARENT
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT
            window.attributes = params
        }

        fun onClick(){
            val intent = WatchContect.buildIntent(Intent(mainactivity,WatchContect::class.java),
                    mainactivity.mInputModel.ipaddr)
            mainactivity.startActivity(intent)
            dismiss()
        }

        fun onCancle(){
            dismiss()
        }

    }

    class InputModel:BaseObservable(){
        @Bindable
        var ipaddr="192.168.8.100"
        set(value) {
            field=value
            notifyChange()
        }
    }
}