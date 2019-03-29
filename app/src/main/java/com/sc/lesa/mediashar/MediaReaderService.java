package com.sc.lesa.mediashar;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;

import android.media.AudioFormat;
import android.media.projection.MediaProjection;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.sc.lesa.mediashar.jlib.P.VideoSender;
import com.sc.lesa.mediashar.jlib.P.VoiceSender;
import com.sc.lesa.mediashar.jlib.media.AacFormat;
import com.sc.lesa.mediashar.jlib.os.HandlerThread;
import com.sc.lesa.mediashar.jlib.server.SocketServerThread;



public class MediaReaderService extends Service {

	boolean serverFlag = false;

	final static String START_SERVER_BEGIN="START_SERVER_BEGIN";
	public final static int START_SERVER=1;
	public final static int STOP_SERVER=2;

	private final int NOTIFICATION_ID_ICON  = 1;
	private static final String UNLOCK_NOTIFICATION_CHANNEL_ID = "unlock_notification";

	private static final String TAG = MediaReaderService.class.getSimpleName();

	HandlerThread handler =new HandlerThread(){
		@Override
		public void handleMessage(Message msg) {
			if (msg.what==START_SERVER){
				stratSendServer();
			}else if (msg.what==STOP_SERVER){
				stopServer();
			}
		}
	};

	SocketServerThread socketServerThread;
	VideoSender videoSender;
	VoiceSender voiceSender;
	static MediaProjection mediaProjection;
	private final IBinder mBinder = new MyBinder(this);


	@Override
	public void onCreate() {
		super.onCreate();
		initNotificationChannel();
		handler.start();
  		Log.d(TAG, "onCreate()");
	}

	private void stratSendServer(){

		buildNotification(R.mipmap.ic_launcher,getString(R.string.app_name),getString(R.string.app_title_runing));
		socketServerThread = new SocketServerThread(9090);
		socketServerThread.start();
		videoSender = new VideoSender(socketServerThread,this,mediaProjection);
		voiceSender =new VoiceSender(socketServerThread,this);

	}

	private void stopServer(){
		videoSender.close();
		voiceSender.exit();
		socketServerThread.exit();
		handler.exit();
		deleteNotification();
		serverFlag=false;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		String flag = intent.getStringExtra(START_SERVER_BEGIN);
		if (flag!=null){
			if (!serverFlag) {
				handler.hasMessages(START_SERVER);
				serverFlag=true;
			}
		}
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
 	 	Log.d(TAG, "onBind()");
		return mBinder;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}

	@Override
	public void onDestroy() {
		Log.d(TAG, "onDestroy()");
		super.onDestroy();
	}

	private void initNotificationChannel(){
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			//创建通知渠道
			CharSequence name = "运行通知";
			String description = "服务运行中";
			String channelId=UNLOCK_NOTIFICATION_CHANNEL_ID;//渠道id
			int importance = NotificationManager.IMPORTANCE_DEFAULT;//重要性级别
			NotificationChannel mChannel = new NotificationChannel(channelId, name, importance);
			mChannel.setDescription(description);//渠道描述
			mChannel.enableLights(false);//是否显示通知指示灯
			mChannel.enableVibration(false);//是否振动

			NotificationManager notificationManager = (NotificationManager) getSystemService(
					NOTIFICATION_SERVICE);
			notificationManager.createNotificationChannel(mChannel);//创建通知渠道
		}

	}

	private  void buildNotification(int resId,String tiile,String contenttext){
		NotificationCompat.Builder builder = new NotificationCompat.Builder(this,UNLOCK_NOTIFICATION_CHANNEL_ID);

		// 必需的通知内容
		builder.setContentTitle(tiile)
				.setContentText(contenttext)
				.setSmallIcon(resId);

		Intent notifyIntent = new Intent(this, MediaReaderService.class);
		PendingIntent notifyPendingIntent = PendingIntent.getActivity(this, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		builder.setContentIntent(notifyPendingIntent);

		Notification notification = builder.build();
		//常驻状态栏的图标
		//notification.icon = resId;
		// 将此通知放到通知栏的"Ongoing"即"正在运行"组中
		notification.flags |=Notification.FLAG_ONGOING_EVENT;
		// 表明在点击了通知栏中的"清除通知"后，此通知不清除，经常与FLAG_ONGOING_EVENT一起使用
		notification.flags |= Notification.FLAG_NO_CLEAR;

		NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		manager.notify(NOTIFICATION_ID_ICON, notification);

		startForeground(NOTIFICATION_ID_ICON, notification);
	}

	private void deleteNotification(){
		NotificationManager nm = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		nm.cancel(NOTIFICATION_ID_ICON);
	}


	public class MyBinder extends Binder {
		MediaReaderService mediaReaderService;

		public MyBinder(){
			super();
		}

		public MyBinder(MediaReaderService m){
			super();
			mediaReaderService=m;
		}

		public void setVideoParam(int width,int height,int videoBitrate,int videoFrameRate){
			VideoSender.setParam(mediaReaderService,width,height,videoBitrate,videoFrameRate);
		}

		/**
		 *
		 * @param ChannelMode {@link AudioFormat#CHANNEL_IN_MONO} 或 {@link AudioFormat#CHANNEL_IN_STEREO}
		 * @param EncodeFormat {@link AudioFormat#ENCODING_PCM_16BIT}
		 * @param ChannelCount {@link AacFormat#ChannleOutOne}
		 * @param ByteRate {@link AacFormat#ByteRate256Kbs}
		 * @param SampleRate {@link AacFormat#SampleRate44100}
		 */
		public void setVoiceParam(int ChannelMode,int EncodeFormat,
								  int ChannelCount,int ByteRate,int SampleRate){
			VoiceSender.setParam(mediaReaderService, ChannelMode, EncodeFormat,
			 ChannelCount,ByteRate,SampleRate);
		}

		public boolean getServerStatus(){
			return mediaReaderService.serverFlag;
		}

		public void stopServer(){
			mediaReaderService.handler.hasMessages(STOP_SERVER);
		}

	}

	public static Intent startServer(Intent intent,MediaProjection mp){
		mediaProjection=mp;
		intent.putExtra(START_SERVER_BEGIN,START_SERVER_BEGIN);
		return intent;
	}

}

