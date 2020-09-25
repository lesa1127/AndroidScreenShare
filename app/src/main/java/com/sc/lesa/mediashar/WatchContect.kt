package com.sc.lesa.mediashar

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.widget.Toast
import com.sc.lesa.mediashar.jlib.server.SocketClientThread
import com.sc.lesa.mediashar.jlib.threads.VideoPlayThread
import com.sc.lesa.mediashar.jlib.threads.VoicePlayThread
import java.io.IOException
import java.util.*
import kotlin.concurrent.thread

class WatchContect : AppCompatActivity(), SurfaceHolder.Callback {
    private lateinit var mSurfaceView: SurfaceView
    lateinit var mSurfaceHolder: SurfaceHolder
    lateinit var ip: String
    lateinit var socketClientThread: SocketClientThread
    var mdiaPlayThread: VideoPlayThread?=null
    var voicePlayThread: VoicePlayThread?=null


    companion object {
        fun buildIntent(intent: Intent, ip: String): Intent {
            intent.putExtra("Address", ip)
            return intent
        }
    }


    override fun onResume() {
        super.onResume()
        val decorView = window.decorView
        decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        val actionBar = supportActionBar
        actionBar!!.hide()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_watch_contect)


        mSurfaceView = findViewById<SurfaceView>(R.id.surfaceView_watch)
        mSurfaceHolder = mSurfaceView.holder
        val intent = intent
        ip = intent.getStringExtra("Address")

        mSurfaceHolder.addCallback(this)
    }

    var preTime: Long = 0
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            val currentTime = Date().time
            if (currentTime - preTime > 2000) {
                Toast.makeText(this, getText(R.string.app_back_exit), Toast.LENGTH_SHORT).show()
                preTime = currentTime
                return true
            }
            clear()
            finish()
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun init() {
        thread(true) {
            socketClientThread = ClientThread(ip)
            try {
                socketClientThread.connect()
            } catch (e: IOException) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this@WatchContect,"${getString(R.string.error)}:${e.message}",Toast.LENGTH_SHORT).show()
                }
                return@thread
            }
            socketClientThread.start()
            mdiaPlayThread = VideoPlayThread(mSurfaceHolder.surface, socketClientThread.dataPackList)
            mdiaPlayThread!!.start()
            voicePlayThread = VoicePlayThread(socketClientThread.dataPackList)
            voicePlayThread!!.start()
        }
    }

    fun clear(){
        socketClientThread.exit()
        mdiaPlayThread?.exit()
        voicePlayThread?.exit()
    }


    private inner class ClientThread(ip:String):SocketClientThread(ip,9090){
        override fun onError(t: Throwable) {
            runOnUiThread {
                Toast.makeText(this@WatchContect,"${getString(R.string.error)}:${t.message}",Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {

    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {

    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        init()
    }

}