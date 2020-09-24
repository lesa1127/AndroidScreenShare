package com.sc.lesa.mediashar

import android.app.Application
import android.media.projection.MediaProjection

class MyApplication : Application() {
    lateinit var mediaProjection: MediaProjection
    var serverStatus=MediaReaderService.ServerStatus.UNSTART
}