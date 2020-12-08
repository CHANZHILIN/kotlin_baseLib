package com.kotlin_baselib.base

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.kotlin_baselib.R
import com.kotlin_baselib.utils.MyLog
import com.kotlin_baselib.utils.SnackBarUtil
import kotlinx.android.synthetic.main.activity_base_toolbar_viewmodel.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import kotlinx.coroutines.TimeoutCancellationException

/**
 * @Description:
 * @Author:         CHEN
 * @CreateDate:     2020/12/8
 */
abstract class BaseToolbarViewModelActivity<VM : BaseViewModel> : BaseActivity() {
    protected lateinit var viewModel: VM

    private var contentLayout: View? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        initVM()
        super.onCreate(savedInstanceState)
        startObserve()
    }

    override fun setContentView() {
        setContentView(R.layout.activity_base_toolbar_viewmodel)
        val layoutRes = getResId()
        if (layoutRes != 0) {
            containerStub?.layoutResource = layoutRes
            contentLayout = containerStub.inflate()
            MyLog.e("-------setContentLayout------$layoutRes")
        } else {
            MyLog.e("-------setContentLayout------null")
        }

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //为了可以在子页面根据需要重写他的点击事件
        setBackClick()

        toolbar?.title = setToolbarTitle()
    }

    private fun setBackClick() {
        toolbar?.setNavigationOnClickListener {
            finish()
        }
    }

    // toolbar
    fun getAppToolbar(): Toolbar? {
        return toolbar
    }

    //toolbar标题
    abstract fun setToolbarTitle(): String?

    override fun setTitle(title: CharSequence?) {
        toolbar?.title = title
    }

    //toolbar右侧文字
    fun setRightTitle(title: CharSequence?) {
        toolbar_rightTitle?.visibility = View.VISIBLE
        toolbar_rightTitle?.text = title
    }


    private fun initVM() {

        providerVMClass()?.let {
            viewModel = ViewModelProvider(ViewModelStoreOwner { viewModelStore }).get(it)
            lifecycle.addObserver(viewModel)
        }
    }

    /**
     * 这个方法一定要重写
     */
//    open fun providerVMClass(): Class<VM>? = null
    abstract fun providerVMClass(): Class<VM>?

    private fun startObserve() {
        //处理一些通用异常，比如网络超时等
        viewModel.run {
            getError().observe(this@BaseToolbarViewModelActivity, Observer {
                requestError(it)
            })
            getFinally().observe(this@BaseToolbarViewModelActivity, Observer {
                requestFinally(it)
            })
        }
    }

    open fun requestFinally(it: Int?) {

    }

    open fun requestError(it: Exception?) {
        //处理一些已知异常
        it?.run {
            when (it) {
                is TimeoutCancellationException -> {
                    SnackBarUtil.shortSnackBar(
                        window.decorView.rootView,
                        "请求超时",
                        SnackBarUtil.WARNING
                    ).show()
                }
                is BaseRepository.TokenInvalidException -> {
                    SnackBarUtil.shortSnackBar(
                        window.decorView.rootView,
                        "登陆超时",
                        SnackBarUtil.WARNING
                    ).show()
                }
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver(viewModel)
    }
}