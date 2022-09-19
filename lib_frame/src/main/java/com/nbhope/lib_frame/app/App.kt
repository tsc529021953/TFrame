package com.nbhope.lib_frame.app

import androidx.annotation.NonNull
import com.nbhope.lib_frame.di.BaseAppComponent
import dagger.android.DispatchingAndroidInjector

/**
 * Created by zhouwentao on 2019-09-03.
 */
interface App {
    @NonNull
    fun getAppComponent(): BaseAppComponent

    fun packageNameList():List<String>
}