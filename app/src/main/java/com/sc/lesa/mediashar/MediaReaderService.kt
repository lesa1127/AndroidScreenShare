package com.sc.lesa.mediashar

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import android.util.Log
import android.widget.Toast
import com.sc.lesa.mediashar.config.Config
import com.sc.lesa.mediashar.jlib.server.SocketServerThread
import com.sc.lesa.mediashar.jlib.threads.VideoSender
import com.sc.lesa.mediashar.jlib.threads.VoiceSender

class MediaReaderService : Service(){

    companion object {
        private val TAG = MediaReaderService::class.java.simpleName
        const val START_SERVER = 1
        const val STOP_SERVER = 2
        private const val UNLOCK_NOTIFICATION_CHANNEL_ID = "unlock_notification"
    }

    var serverStatus = STOP_SERVER
    private val NOTIFICATION_ID_ICON = 1

    lateinit var socketServerThread: SocketServerThread
    lateinit var videoSender: VideoSender
    lateinit var voiceSender: VoiceSender
    lateinit var myApplication: MyApplication
    val handler=Handler()

    override fun onCreate() {
        super.onCreate()
        myApplication=application as MyApplication
        initNotificationChannel()
        Log.d(TAG, "onCreate()")
    }

    private fun stratSendServer() {
        serverStatus = START_SERVER
        buildNotification(R.mipmap.ic_launcher, getString(R.string.app_name), getString(R.string.app_title_runing))
        socketServerThread = SendThread()
        socketServerThread.start()
        val config: Config = Config.getConfig(this)
         try {
            videoSender=VideoSender(socketServerThread, myApplication.mediaProjection,
                config.width.toInt(), config.height.toInt(),
                config.videoBitrate.toInt(), config.videoFrameRate.toInt()
            )
            voiceSender = VoiceSender(socketServerThread,
                 config.channelMode, config.encodeFormat, config.channelCount.toInt(),
                 config.voiceByteRate.toInt(), config.voiceSampleRate.toInt()
            )

        } catch (throwable: Throwable) {
            throwable.printStackTrace()
            return
        }
    }

    private fun stopServer() {
        videoSender.exit()
        voiceSender.exit()
        socketServerThread.exit()
        deleteNotification()
        serverStatus = STOP_SERVER
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val cmd=intent.getIntExtra("CMD",0)
        when(cmd){
            START_SERVER->{
                stratSendServer()
            }
            STOP_SERVER->{
                stopServer()
            }
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder {
        throw Exception("unable to bind!")
    }



    override fun onDestroy() {
        Log.d(TAG, "onDestroy()")
        super.onDestroy()
    }

    private fun initNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //创建通知渠道
            val name: CharSequence = "运行通知"
            val description = "服务运行中"
            val channelId = UNLOCK_NOTIFICATION_CHANNEL_ID //渠道id
            val importance = NotificationManager.IMPORTANCE_DEFAULT //重要性级别
            val mChannel = NotificationChannel(channelId, name, importance)
            mChannel.description = description //渠道描述
            mChannel.enableLights(false) //是否显示通知指示灯
            mChannel.enableVibration(false) //是否振动
            val notificationManager = getSystemService(
                    Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel) //创建通知渠道
        }
    }

    private fun buildNotification(resId: Int, tiile: String, contenttext: String) {
        val builder = NotificationCompat.Builder(this, UNLOCK_NOTIFICATION_CHANNEL_ID)

        // 必需的通知内容
        builder.setContentTitle(tiile)
                .setContentText(contenttext)
                .setSmallIcon(resId)
        val notifyIntent = Intent(this, MediaProjectionActivity::class.java)
        val notifyPendingIntent = PendingIntent.getService(this, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        builder.setContentIntent(notifyPendingIntent)
        val notification = builder.build()
        //常驻状态栏的图标
        //notification.icon = resId;
        // 将此通知放到通知栏的"Ongoing"即"正在运行"组中
        notification.flags = notification.flags or Notification.FLAG_ONGOING_EVENT
        // 表明在点击了通知栏中的"清除通知"后，此通知不清除，经常与FLAG_ONGOING_EVENT一起使用
        notification.flags = notification.flags or Notification.FLAG_NO_CLEAR
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIFICATION_ID_ICON, notification)
        startForeground(NOTIFICATION_ID_ICON, notification)
    }

    private fun deleteNotification() {
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.cancel(NOTIFICATION_ID_ICON)
    }


    enum class ServerStatus{
        UNSTART,
        STARTED
    }

    private inner class SendThread:SocketServerThread(9090) {
        override fun onError(t: Throwable) {
            myApplication.serverStatus=ServerStatus.UNSTART
            handler.post {
                Toast.makeText(this@MediaReaderService,"${getString(R.string.server_error_start)}:${t.message}",Toast.LENGTH_SHORT).show()
            }
        }
    }


}