package com.kotlin_baselib.base

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner

/**
 *  Created by CHEN on 2019/8/28
 *  Email:1181785848@qq.com
 *  Package:com.kotlin_mvvm_library.base
 *  Introduce:
 **/
abstract class BaseViewModelFragment<VM : BaseViewModel> :BaseFragment() {


    protected lateinit var viewModel: VM


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initVM()
        super.onViewCreated(view, savedInstanceState)
        startObserve()
    }

    private fun initVM() {
        providerVMClass()?.let {
            viewModel = ViewModelProvider(ViewModelStoreOwner { viewModelStore }).get(it)
            lifecycle.addObserver(viewModel)
        }
    }

    open fun providerVMClass(): Class<VM>? = null
    open fun startObserve() {}


    override fun onDestroy() {
        super.onDestroy()
        if (this::viewModel.isInitialized)
            lifecycle.removeObserver(viewModel)
    }

}