package com.kotlin_baselib.base

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.kotlin_baselib.utils.SnackBarUtil
import kotlinx.coroutines.TimeoutCancellationException

/**
 *  Created by CHEN on 2019/8/28
 *  Email:1181785848@qq.com
 *  Package:com.kotlin_mvvm_library.base
 *  Introduce:
 **/
abstract class BaseViewModelActivity<VM : BaseViewModel> : BaseActivity() {

    protected lateinit var viewModel: VM

    override fun onCreate(savedInstanceState: Bundle?) {
        initVM()
        super.onCreate(savedInstanceState)
        startObserve()
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
    open fun providerVMClass(): Class<VM>? = null

    private fun startObserve() {
        //处理一些通用异常，比如网络超时等
        viewModel.run {
            getError().observe(this@BaseViewModelActivity, Observer {
                requestError(it)
            })
            getFinally().observe(this@BaseViewModelActivity, Observer {
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
                    SnackBarUtil.shortSnackBar(window.decorView.rootView, "请求超时", SnackBarUtil.WARNING).show()
                }
                is BaseRepository.TokenInvalidException -> {
                    SnackBarUtil.shortSnackBar(window.decorView.rootView, "登陆超时", SnackBarUtil.WARNING).show()
                }
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver(viewModel)
    }
}