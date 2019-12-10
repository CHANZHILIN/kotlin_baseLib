package com.kotlin_baselib.mvvmbase

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.kotlin_baselib.entity.EmptyEntity

/**
 *  Created by CHEN on 2019/8/28
 *  Email:1181785848@qq.com
 *  Package:com.mvvmbase
 *  Introduce:
 **/
class EmptyViewModel : BaseViewModel() {

    private val versionData: MutableLiveData<EmptyEntity> by lazy {
        MutableLiveData<EmptyEntity>().also {
            loadDatas()
        }
    }

    private val repository = EmptyRepository()

    fun getActicle(): LiveData<EmptyEntity> {
        return versionData
    }

    private fun loadDatas() = launchUI {
        val result = repository.getVersionData()
        versionData.value = result.data
    }

}