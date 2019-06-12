package com.kotlin_baselib.base

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kotlin_baselib.R
import com.kotlin_baselib.loadingview.LoadingView

/**
 *  Created by CHEN on 2019/6/12
 *  Email:1181785848@qq.com
 *  Package:com.kotlin_baselib.base
 *  Introduce:
 **/
abstract class BaseFragment : Fragment() {

    protected var mContext: BaseActivity? = null
    protected var mRootView: View? = null

    protected var mloadingDialog: AlertDialog? = null
    protected var mLoadingView: LoadingView? = null

    /**
     * 视图是否加载完毕
     */
    private var isViewPrepare = false
    /**
     * 数据是否加载过了
     */
    private var hasLoadData = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = activity as BaseActivity;
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = setContentView(inflater, getResId())
        val dialogView = LayoutInflater.from(mContext).inflate(R.layout.layout_loading_view, null)
        mLoadingView = dialogView.findViewById<LoadingView>(R.id.loading_view)
        mloadingDialog = AlertDialog
                .Builder(mContext!!, R.style.CustomDialog)
                .setView(dialogView)
                .setCancelable(true)
                .create()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isViewPrepare = true
        initData()
        initListener()
        lazyLoadDataIfPrepared()
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            lazyLoadDataIfPrepared()
        }
    }

    private fun lazyLoadDataIfPrepared() {
        if (userVisibleHint && isViewPrepare && !hasLoadData) {
            lazyLoad()
            hasLoadData = true
        }
    }


    fun setContentView(inflater: LayoutInflater, resId: Int): View? {
        if (mRootView == null) {
            mRootView = inflater.inflate(resId, null)
        }
        val parent: ViewGroup? = (mRootView?.parent) as ViewGroup
        if (parent != null) {
            parent.removeView(mRootView)
        }
        return mRootView
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

    /**
     * 懒加载
     */
    abstract fun lazyLoad()


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
        mContext!!.finish()
    }

    protected fun finishResult(intent: Intent) {
        mContext!!.setResult(AppCompatActivity.RESULT_OK, intent)
        mContext!!.finish()
    }

    protected fun finishResult() {
        mContext!!.setResult(AppCompatActivity.RESULT_OK)
        mContext!!.finish()
    }

}