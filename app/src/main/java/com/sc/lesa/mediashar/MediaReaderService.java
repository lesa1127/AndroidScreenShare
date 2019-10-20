package com.sc.lesa.mediashar;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.sc.lesa.mediashar.jlib.P.VideoSender;
import com.sc.lesa.mediashar.jlib.P.VoiceSender;
import com.sc.lesa.mediashar.jlib.server.SocketServerThread;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MediaReaderService extends Service {
	private static final String TAG = MediaReaderService.class.getSimpleName();

	int serverStatus=STOP_SERVER;

	final static String START_SERVER_SHARE="START_SERVER_BEGIN";
	final static String STOP_SERVER_SHARE="STOP_SERVER_SHARE";

	public final static int START_SERVER=1;
	public final static int STOP_SERVER=2;

	private final int NOTIFICATION_ID_ICON  = 1;
	private static final String UNLOCK_NOTIFICATION_CHANNEL_ID = "unlock_notification";


	ExecutorService executorService  = Executors.newSingleThreadExecutor();

	SocketServerThread socketServerThread;
	VideoSender videoSender;
	VoiceSender voiceSender;

	private final IBinder mBinder = new MyBinder(this);
	private Handler handler = new Handler();


	@Override
	public void onCreate() {
		super.onCreate();
		initNotificationChannel();
  		Log.d(TAG, "onCreate()");
	}

	private void stratSendServer(){
		serverStatus=START_SERVER;
		handler.post(()->{
			buildNotification(R.mipmap.ic_launcher,getString(R.string.app_name),getString(R.string.app_title_runing));
		});
		socketServerThread = new SocketServerThread(9090);
		socketServerThread.start();
		Config config = Config.Companion.getConfig(this);
		try {
			videoSender = new VideoSender(socketServerThread, MyApplication.getMediaProjection(),
					config.getWidth(),config.getHeight(),
					config.getVideoBitrate(),config.getVideoFrameRate()
			);
		}catch (Throwable throwable){
			throwable.printStackTrace();
			System.exit(1);
		}
		voiceSender =new VoiceSender(socketServerThread,
				config.getChannelMode(),config.getEncodeFormat(),config.getChannelCount(),
				config.getVoiceByteRate(),config.getVoiceSampleRate()
		);

	}

	private void stopServer(){
		videoSender.close();
		voiceSender.exit();
		socketServerThread.exit();
		handler.post(()->{
			deleteNotification();
		});
		serverStatus=STOP_SERVER;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
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

		Intent notifyIntent = new Intent(this, MediaProjectionActivity.class);
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


		public int getServerStatus(){
			return mediaReaderService.serverStatus;
		}

		public void stopShare(){
			if (getServerStatus()==START_SERVER) {
				mediaReaderService.executorService.execute(() -> {
					stopServer();
				});
			}
		}

		public void startShare(){
			if (getServerStatus()==STOP_SERVER) {
				mediaReaderService.executorService.execute(()->{
					stratSendServer();
				});
			}

		}

	}



}

