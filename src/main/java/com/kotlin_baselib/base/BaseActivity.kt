package com.kotlin_baselib.base

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import com.kotlin_baselib.R
import com.kotlin_baselib.loadingview.LoadingView
import com.kotlin_baselib.utils.AndroidBugWorkaround

/**
 *  Created by CHEN on 2019/6/12
 *  Email:1181785848@qq.com
 *  Package:com.kotlin_baselib.base
 *  Introduce:
 **/
abstract class BaseActivity<V : BaseView, M : BaseModel, P : BasePresenter<V, M>> : BaseView, AppCompatActivity() {
    protected lateinit var mContext: BaseActivity<V, M, P>
    protected lateinit var mloadingDialog: AlertDialog
    protected lateinit var mLoadingView: LoadingView

    protected var mToolbar: Toolbar? = null

    protected lateinit var mPresenter: P

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*   if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
               window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
           }*/

        mContext = this
        preSetContentView()     //在设置contentView时候，干一些事情，需要时重载
        setContentView(getResId())
        AndroidBugWorkaround.assistActivity(this)       //解决虚拟导航栏遮盖问题
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        val dialogView = LayoutInflater.from(mContext).inflate(R.layout.layout_loading_view, null)
        mLoadingView = dialogView.findViewById<LoadingView>(R.id.loading_view)
        mloadingDialog = AlertDialog
                .Builder(mContext, R.style.CustomDialog)
                .setView(dialogView)
                .setCancelable(true)
                .create()

        mToolbar = findViewById(R.id.toolbar)

        setSupportActionBar(mToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mPresenter = createPresenter()

        // 这里把标题栏回退点击事件放在initEvent之前，是为了可以在子页面根据需要重写他的点击事件
        setBackClick()

        initData()
        initListener()
    }

    private fun setBackClick() {
        mToolbar?.setNavigationOnClickListener {
            finish()
        }
    }

    protected fun setTitle(title: String) {
        runOnUiThread {
            if (mToolbar != null)
                mToolbar!!.title = title
        }

    }

    /**
     * @return 返回具体的Persenter
     */
    protected abstract fun createPresenter(): P

    /**
     * 获取资源id
     */
    protected abstract fun getResId(): Int

    /**
     * 初始化数据
     */
    protected abstract fun initData()

    /**
     * 初始化点击事件
     */
    protected abstract fun initListener()

    open fun preSetContentView() {}


    override fun onPause() {
        super.onPause()
        hideLoading()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mloadingDialog.isShowing) {
            mloadingDialog.dismiss()
        }
        mPresenter.detachView()
    }

    /**
     * 弹出加载动画
     */
    protected fun showLoading() {
        if (!mloadingDialog.isShowing) {
            mloadingDialog.show()
            mLoadingView.start()
        }
    }

    /**
     * 隐藏加载动画
     */
    protected fun hideLoading() {
        if (mloadingDialog.isShowing) {
            mloadingDialog.dismiss()
            mLoadingView.reset()
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

    override fun onSuccess(msg: String) {
        hideLoading()
    }

    override fun onError(code: Int, msg: String) {
        hideLoading()
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show()
    }
}
