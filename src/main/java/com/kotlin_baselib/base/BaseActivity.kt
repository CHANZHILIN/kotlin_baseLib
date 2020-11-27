package com.kotlin_baselib.base

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import androidx.annotation.CallSuper
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.alibaba.android.arouter.launcher.ARouter
import com.kotlin_baselib.R
import com.kotlin_baselib.loadingview.LoadingView
import com.kotlin_baselib.utils.ScreenUtils
import com.kotlin_baselib.utils.StatusBarUtil

/**
 *  Created by CHEN on 2019/8/28
 *  Email:1181785848@qq.com
 *  Package:com.kotlin_mvvm_library.base
 *  Introduce:
 **/
abstract class BaseActivity : AppCompatActivity() {

    protected lateinit var mContext: BaseActivity
    protected lateinit var mloadingDialog: AlertDialog
    protected lateinit var mLoadingView: LoadingView

    protected var mToolbar: Toolbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = this
        ScreenUtils.init(this)
        setupTheme()
//        preSetContentView()//在设置contentView时候，干一些事情，需要时重载
        // 主题、样式设置
        setContentView(getResId())
        ARouter.getInstance().inject(this)
//        AndroidBugWorkaround.assistActivity(this)       //解决虚拟导航栏遮盖问题

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

        // 这里把标题栏回退点击事件放在initEvent之前，是为了可以在子页面根据需要重写他的点击事件
        setBackClick()

        initData()
        initListener()

        if (reMoveBlack()) {
            StatusBarUtil.removeBlackFont(this)
        }
    }

    protected fun startActivity(z: Class<*>) {
        startActivity(Intent(applicationContext, z))
    }

    private fun setBackClick() {
        mToolbar?.setNavigationOnClickListener {
            finish()
        }
    }

//    open fun preSetContentView() {}

    /**
     * 页面风格设置 -- 模板方法
     * 默认根据[.isTransparentPage]返回值进行设置页面是否透明（沉浸式）
     */
    @CallSuper
    protected open fun setupTheme() {
        when {
            isTransparentPage() -> {
                StatusBarUtil.setTransparentForWindowAndDarkIcon(this, isHideNavigationBar())
            }
            Build.VERSION.SDK_INT >= 23 -> {
                StatusBarUtil.setLightModeOnly(this)
            }
            Build.VERSION.SDK_INT >= 21 -> {
                StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.white), 80)
            }
        }
    }

    /**
     * 设置是否透明页面
     */
    protected open fun isTransparentPage(): Boolean {
        return false
    }

    /**
     * 是否移除黑色字体，状态栏默认是黑色字体
     */
    protected open fun reMoveBlack(): Boolean {
        return true
    }

    /**
     * 设置是否隐藏导航栏
     */
    protected open fun isHideNavigationBar(): Boolean {
        return false
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
     * 初始化监听事件
     */
    abstract fun initListener()

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

    override fun onPause() {
        super.onPause()
        hideLoading()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mloadingDialog.isShowing) {
            mloadingDialog.dismiss()
        }
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


}