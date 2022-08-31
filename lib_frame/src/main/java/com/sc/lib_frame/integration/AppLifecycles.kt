package com.sc.lib_frame.integration

import android.app.Application
import android.content.Context
import androidx.annotation.NonNull

/**
 * Created by zhouwentao on 2019-09-03.
 */
interface AppLifecycles {
    fun attachBaseContext(@NonNull base: Context)

    fun onCreate(@NonNull application:Application)

    fun onTerminate(@NonNull application: Application)
}