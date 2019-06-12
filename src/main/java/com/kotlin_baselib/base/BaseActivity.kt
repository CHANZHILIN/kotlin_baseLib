package com.kotlin_baselib.base

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import com.kotlin_baselib.R
import com.kotlin_baselib.loadingview.LoadingView

/**
 *  Created by CHEN on 2019/6/12
 *  Email:1181785848@qq.com
 *  Package:com.kotlin_baselib.base
 *  Introduce:
 **/
abstract class BaseActivity : AppCompatActivity() {

    protected var mContext: BaseActivity? = null
    protected var mloadingDialog: AlertDialog? = null
    protected var mLoadingView: LoadingView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*   if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
               window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
           }*/
        mContext = this
        setContentView(getResId())
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        val dialogView = LayoutInflater.from(mContext).inflate(R.layout.layout_loading_view, null)
        mLoadingView = dialogView.findViewById<LoadingView>(R.id.loading_view)
        mloadingDialog = AlertDialog
                .Builder(mContext!!, R.style.CustomDialog)
                .setView(dialogView)
                .setCancelable(true)
                .create()
        initData()
        initListener()
    }

    /**
     * 获取资源id
     */
    abstract fun getResId(): Int

    /**
     * 初始化数据
     */
    abstract fun initData()

    /**
     * 初始化点击事件
     */
    abstract fun initListener()


    override fun onPause() {
        super.onPause()
        hideLoading()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mloadingDialog != null) {
            mloadingDialog = null
        }
    }

    /**
     * 弹出加载动画
     */
    protected fun showLoading() {
        if (!mloadingDialog!!.isShowing) {
            mloadingDialog!!.show()
            mLoadingView!!.start()
        }
    }

    /**
     * 隐藏加载动画
     */
    protected fun hideLoading() {
        if (mloadingDialog!!.isShowing) {
            mloadingDialog!!.dismiss()
            mLoadingView!!.reset()
        }
    }

    /**
     * 不带动画结束
     */
    protected fun finishSimple() {
        super.finish()
    }

    protected fun finishResult(intent: Intent) {
        setResult(RESULT_OK, intent)
        this.finish()
    }

    protected fun finishResult() {
        setResult(RESULT_OK)
        this.finish()
    }
}
