# AndroidScreenShare
Share your screen and voice to other phone. <br>
共享你的屏幕和音频到另一台手机 具体分辨率,帧率,声道,和采样率因机型而定 手动狗头.

### Demo 安装包 :
[点击下载](https://github.com/lesa1127/AndroidScreenShare/blob/master/info/app-release.apk?raw=true)

### 共享原理
主要是使用了Android 5.0 上的MediaProjection 创建虚拟屏幕并采集录制,然后调用系统自带的H264编码器之后封装发送到另一台手机.

### 使用方式 :

* 启动应用程序并给予所需的权限根据需要点击分享或者观看.

![demo](https://github.com/lesa1127/AndroidScreenShare/blob/master/info/1.png?raw=true)

* 接下来选择开始分析或者分享设置.

![demo](https://github.com/lesa1127/AndroidScreenShare/blob/master/info/2.png?raw=true)

* 注意参数的设置 不同的机型有不同的编码上限 比如虚拟机的分辨率上限 最好别超过1080P.同时降低分辨率有助于降低发热.同时声道数最大为2,
一般情况下降噪麦克风会占用一个所以默认为1.

![demo](https://github.com/lesa1127/AndroidScreenShare/blob/master/info/3.png?raw=true)
