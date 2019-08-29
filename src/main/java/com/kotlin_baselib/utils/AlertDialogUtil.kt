package com.kotlin_baselib.utils

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.widget.EditText
import com.kotlin_baselib.R

/**
 *  Created by CHEN on 2019/7/11
 *  Email:1181785848@qq.com
 *  Package:com.kotlin_baselib.utils
 *  Introduce: 弹出框
 **/
class AlertDialogUtil {
    companion object {
        private var mAlertDialogUtil: AlertDialogUtil? = null
        private var builder: AlertDialog.Builder? = null
        fun getInstance(mContext: Context): AlertDialogUtil {
            if (mAlertDialogUtil == null) {
                synchronized(this) {
                    mAlertDialogUtil = AlertDialogUtil()
                    builder = AlertDialog.Builder(mContext, R.styleable.AppCompatTheme_alertDialogStyle)
                }
            }
            return mAlertDialogUtil!!
        }
    }

    /**
     * 普通对话框
     */
    fun showAlertDialog(message: String, nevigationButton: String, positiveButton: String, dialogInterface1: DialogInterface.OnClickListener, dialogInterface2: DialogInterface.OnClickListener) {
        builder?.setMessage(message)
        builder?.setNegativeButton(nevigationButton, dialogInterface1)
        builder?.setPositiveButton(positiveButton, dialogInterface2)
        builder?.show()
    }

}