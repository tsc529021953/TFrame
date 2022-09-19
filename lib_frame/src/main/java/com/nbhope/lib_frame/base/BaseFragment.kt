package com.nbhope.lib_frame.base

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import com.alibaba.android.arouter.launcher.ARouter
import com.nbhope.lib_frame.di.AndroidInjectionDelegate
import com.nbhope.lib_frame.di.Injectable
import com.nbhope.lib_frame.utils.HopeUtils
import timber.log.Timber

/**
 * Created by zhouwentao on 2019-08-28.
 */
abstract class BaseFragment : Fragment(), Injectable {

    protected val TAG: String = this.javaClass.simpleName

    protected abstract var layoutId: Int

    protected var rootView: View? = null

    protected open fun initParam(savedInstanceState: Bundle?) {

    }

    /**
     * liveData订阅
     */
    protected abstract fun subscribeUi()


    /**
     * 拿数据
     */
    protected abstract fun initData()

    companion object {

        /**
         * 创建fragment的静态方法，方便传递参数
         *
         * @param args 传递的参数
         * @return
         */
        fun <T : Fragment> newInstance(clazz: Class<*>, @NonNull args: Bundle): T {
            var mFragment: T? = null
            try {
                mFragment = clazz.newInstance() as T
            } catch (e: java.lang.InstantiationException) {
                e.printStackTrace()
                Timber.e(e)
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
                Timber.e(e)
            }

            mFragment!!.arguments = args
            return mFragment
        }

        /**
         * 无参创建fragment的静态方法
         *
         * @return
         */
        fun <T : Fragment> newInstance(clazz: Class<*>): T {
            var mFragment: T? = null
            try {
                mFragment = clazz.newInstance() as T
            } catch (e: java.lang.InstantiationException) {
                e.printStackTrace()
                Timber.e(e)
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
                Timber.e(e)
            }

            return mFragment!!
        }


    }

    open fun initInject(){
        AndroidInjectionDelegate.inject(this)
        ARouter.getInstance().inject(this)
    }

    override fun onAttach(activity: Activity) {
        initInject()
        super.onAttach(activity)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        rootView = if (this !is BaseBindingFragment<*, *>) {
            inflater.inflate(layoutId, container, false)
        }
        //这里在BaseBindingFragment里做初始化
        else {
            null
        }
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initParam(savedInstanceState)
        subscribeUi()
        initData()
        /**
         * 后期加入全局网络错误等状态改变管理
         */
    }

    open fun showDialog(title: String) {

    }

    open fun dismissDialog() {

    }

    fun launchActivity(arouterPath: String, bundle: Bundle? = null) {
        HopeUtils.startActivityByArouter(arouterPath,requireContext(), bundle)
    }


}