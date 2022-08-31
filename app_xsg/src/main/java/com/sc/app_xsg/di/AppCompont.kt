package com.sc.app_xsg.di

import android.app.Application
import com.sc.app_xsg.app.AppHope
import com.sc.lib_frame.app.HopeBaseApp
import com.sc.lib_frame.di.BaseAppComponent
import com.sc.lib_frame.di.scope.AppScope
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.DispatchingAndroidInjector

@AppScope
@Component(
    modules = [
        AndroidInjectionModule::class,
//            IndexModule::class,
        ActivityBindModule::class,
//            MoreLibModule::class,
        ActivityViewModelModule::class
    ],
    dependencies = [BaseAppComponent::class]
)
interface AppComponent {
    val application: Application

    val androidInjector: DispatchingAndroidInjector<Any>

    @Component.Builder
    interface Builder {

        fun appComponent(appComponent: BaseAppComponent): Builder

        fun build(): AppComponent
    }

    fun inject(hopeApp: AppHope)
}