package com.kotlin_baselib.base

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.kotlin_baselib.R

class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
    }
}
