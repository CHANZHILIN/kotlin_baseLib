package com.kotlin_baselib.base


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.kotlin_baselib.R
import com.kotlin_baselib.loadingview.LoadingView

abstract class BaseFragment : Fragment() {
    protected lateinit var mContext: BaseActivity

    protected lateinit var mloadingDialog: AlertDialog
    protected lateinit var mLoadingView: LoadingView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(getResId(), container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val dialogView = LayoutInflater.from(mContext).inflate(R.layout.layout_loading_view, null)
        mLoadingView = dialogView.findViewById<LoadingView>(R.id.loading_view)
        mloadingDialog = AlertDialog
            .Builder(mContext, R.style.CustomDialog)
            .setView(dialogView)
            .setCancelable(true)
            .create()
        initData()
        initListener()
        super.onViewCreated(view, savedInstanceState)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = activity as BaseActivity
    }

    /**
     * 必须实现的方法
     */
    abstract fun getResId(): Int

    abstract fun initListener()

    abstract fun initData()
    protected fun startActivity(z: Class<*>) {
        startActivity(Intent(mContext, z))
    }

    /**
     * 不带动画结束
     */
    protected fun finishSimple() {
        mContext.finish()
    }

    protected fun finishResult(intent: Intent) {
        mContext.setResult(AppCompatActivity.RESULT_OK, intent)
        mContext.finish()
    }

    protected fun finishResult() {
        mContext.setResult(AppCompatActivity.RESULT_OK)
        mContext.finish()
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
