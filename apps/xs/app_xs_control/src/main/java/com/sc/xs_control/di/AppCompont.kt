package com.sc.xs_control.di

import android.app.Application
import com.sc.xs_control.app.AppHope
import com.nbhope.lib_frame.di.BaseAppComponent
import com.nbhope.lib_frame.di.scope.AppScope
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.DispatchingAndroidInjector

@AppScope
@Component(
    modules = [
        AndroidInjectionModule::class,
        ActivityBindModule::class,
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
