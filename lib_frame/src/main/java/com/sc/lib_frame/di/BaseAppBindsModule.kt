package com.sc.lib_frame.di

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import com.sc.lib_frame.app.ActivityLifecycleImpl
import com.sc.lib_frame.viewmodel.HopeViewModelFactory
import dagger.Binds
import dagger.Module
import javax.inject.Named
import javax.inject.Singleton

/**
 * Created by zhouwentao on 2019-08-28.
 * 由于kotlin限制
 * @Binds 和 @Providers不能放在一起
 */
@Module
abstract class BaseAppBindsModule {

    @Binds
    @Singleton
    abstract fun bindActivityLifecycle(activityLifecycleImpl: ActivityLifecycleImpl): Application.ActivityLifecycleCallbacks

    @Binds
    abstract fun bindViewModelFactory(factory: HopeViewModelFactory): ViewModelProvider.Factory
}
