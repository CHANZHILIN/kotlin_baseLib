package com.kotlin_baselib.utils

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog
import java.lang.Exception

/**
 *  Created by CHEN on 2019/7/11
 *  Email:1181785848@qq.com
 *  Package:com.kotlin_baselib.utils
 *  Introduce: 弹出框
 **/
object AlertDialogUtil {

    /**
     * 普通对话框
     */
    fun showAlertDialog(
        mContext: Context?,
        message: String,
        nevigationButton: String,
        positiveButton: String,
        dialogInterface1: DialogInterface.OnClickListener,
        dialogInterface2: DialogInterface.OnClickListener
    ) {
        if (mContext == null) throw Exception("context is null")
        AlertDialog.Builder(mContext).apply {
            setMessage(message)
            setNegativeButton(nevigationButton, dialogInterface1)
            setPositiveButton(positiveButton, dialogInterface2)
            show()
        }
    }


}