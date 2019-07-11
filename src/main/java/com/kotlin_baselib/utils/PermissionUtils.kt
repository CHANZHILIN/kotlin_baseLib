package com.kotlin_baselib.utils

import android.Manifest
import android.app.Activity
import android.app.Fragment
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.annotation.RequiresApi
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import com.kotlin_baselib.application.BaseApplication
import java.util.*

/**
 * detail: 权限请求工具类
 * Created by Ttt
 * hint:
 * -
 * 参考:
 * https://github.com/anthonycr/Grant
 * compile 'com.anthonycr.grant:permissions:1.0'
 * -
 * 权限介绍
 * https://www.cnblogs.com/mengdd/p/4892856.html
 * -
 * 第三方库:
 * PermissionsDispatcher: https://github.com/hotchemi/PermissionsDispatcher
 * RxPermissions: https://github.com/tbruyelle/RxPermissions
 * Grant: https://github.com/anthonycr/Grant
 * -
 * 使用方法
 * // 第一种请求方式
 * PermissionUtils.permission("").callBack(null).request();
 * // 第二种请求方式 - 需要在 onRequestPermissionsResult 中通知调用
 * PermissionUtils.permission("").callBack(null).request(Activity);
 * ======
 * 注意事项: 需要注意在onResume 中调用
 * 不管是第一种方式, 跳自定义的Activity, 还是第二种 系统内部跳转授权页面, 都会多次触发onResume
 * https://www.aliyun.com/jiaocheng/8030.html
 * 尽量避免在 onResume中调用
 * com.anthonycr.grant:permissions:1.0 也是会触发onResume 只是 通过 Set<String> mPendingRequests 来控制请求过的权限
 * 拒绝后在onResume 方法内再次请求, 直接触发授权成功, 如果需要清空通过调用 notifyPermissionsChange 通知改变, 否则一直调用获取权限，拒绝过后，都会认为是请求通过
</String> */
class PermissionUtils
// =

/**
 * 构造函数
 *
 * @param permissions
 */
private constructor(vararg permissions: String) {
    // 判断是否请求过
    private var isRequest = false
    /**
     * 申请的权限
     */
    private val mPermissions = ArrayList<String>()
    /**
     * 准备请求的权限
     */
    private val mPermissionsRequest = ArrayList<String>()
    /**
     * 申请通过的权限
     */
    private val mPermissionsGranted = ArrayList<String>()
    /**
     * 申请未通过的权限
     */
    private val mPermissionsDenied = ArrayList<String>()
    /**
     * 申请未通过的权限 - 永久拒绝
     */
    private val mPermissionsDeniedForever = ArrayList<String>()
    /**
     * 查询不到的权限
     */
    private val mPermissionsNotFound = ArrayList<String>()
    /**
     * 操作回调
     */
    private var mCallBack: PermissionCallBack? = null
    /**
     * 回调方法
     */
    private val mLooper = Looper.getMainLooper()

    init {
        mPermissions.clear()
        // 防止数据为null
        if (permissions != null && permissions.size != 0) {
            // 遍历全部需要申请的权限
            for (permission in permissions) {
                mPermissions.add(permission)
            }
        }
    }

    /**
     * 设置回调方法
     *
     * @param callBack
     */
    fun callBack(callBack: PermissionCallBack): PermissionUtils {
        if (isRequest) {
            return this
        }
        this.mCallBack = callBack
        return this
    }

    /**
     * 权限判断处理
     *
     * @return -1 已经请求过, 0 = 不处理, 1 = 需要请求
     */
    private fun checkPermissions(): Int {
        if (isRequest) {
            return -1 // 已经申请过
        }
        isRequest = true
        // 如果 SDK 版本小于 23 则直接通过
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            // 表示全部权限都通过
            mPermissionsGranted.addAll(mPermissions)
            // 处理请求回调
            requestCallback()
        } else {
            for (permission in mPermissions) {
                // 首先判断是否存在
                if (mAllPermissions.contains(permission)) {
                    // 判断是否通过请求
                    if (isGranted(BaseApplication.instance, permission)) {
                        mPermissionsGranted.add(permission) // 权限允许通过
                    } else {
                        mPermissionsRequest.add(permission) // 准备请求权限
                    }
                } else {
                    // 保存到没找到的权限集合
                    mPermissionsNotFound.add(permission)
                }
            }
            // 判断是否存在等待请求的权限
            if (mPermissionsRequest.isEmpty()) {
                // 处理请求回调
                requestCallback()
            } else { // 表示需要申请
                return 1
            }
        }
        // 表示不需要申请
        return 0
    }

    /**
     * 请求权限
     * --
     * 内部自动调用 PermissionUtils.isGranted, 并且进行判断处理
     * 无需调用以下代码判断
     * boolean isGranted = PermissionUtils.isGranted(Manifest.permission.xx);
     */
    fun request() {
        if (checkPermissions() == 1) {
            // 如果 SDK 版本大于 23 才请求
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                sInstance = this
                // 自定义Activity
                PermissionUtils.PermissionActivity.start(BaseApplication.instance)
            }
        }
    }

    /**
     * 请求权限 - 需要在Activity 的 onRequestPermissionsResult 回调中 调用 PermissionUtils.onRequestPermissionsResult(this);
     *
     * @param activity    [Fragment.getActivity]
     * @param requestCode
     */
    @JvmOverloads
    fun request(activity: Activity?, requestCode: Int = P_REQUEST_CODE) {
        if (checkPermissions() == 1 && activity != null) {
            // 如果 SDK 版本大于 23 才请求
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                sInstance = this
                // 请求权限
                val permissions = mPermissionsRequest.toTypedArray()
                // 请求权限
                ActivityCompat.requestPermissions(activity, permissions, requestCode)
            }
        }
    }

    // == 请求权限回调 ==

    interface PermissionCallBack {
        /**
         * 授权通过权限
         *
         * @param permissionUtils
         */
        fun onGranted(permissionUtils: PermissionUtils)

        /**
         * 授权未通过权限
         *
         * @param permissionUtils
         */
        fun onDenied(permissionUtils: PermissionUtils)
    }

    // 实现Activity的透明效果
    // https://blog.csdn.net/u014434080/article/details/52260407
    @RequiresApi(api = Build.VERSION_CODES.M)
    class PermissionActivity : Activity() {

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            // 请求权限
            val size = sInstance!!.mPermissionsRequest.size
            requestPermissions(sInstance!!.mPermissionsRequest.toTypedArray(), 1)
        }

        override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
            sInstance!!.onRequestPermissionsResultCommon(this) // 处理回调
            finish() // 关闭当前页面
        }

        companion object {

            fun start(context: Context) {
                val starter = Intent(context, PermissionActivity::class.java)
                starter.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(starter)
            }
        }
    }

    // == 内部处理方法 ==

    /**
     * 内部请求回调, 统一处理方法
     */
    private fun requestCallback() {
        if (mCallBack != null) {
            // 判断是否允许全部权限
            val isGrantedAll = mPermissions.size == mPermissionsGranted.size
            // 允许则触发回调
            if (isGrantedAll) {
                Handler(mLooper).post { mCallBack!!.onGranted(this@PermissionUtils) }
            } else {
                Handler(mLooper).post { mCallBack!!.onDenied(this@PermissionUtils) }
            }
        }
    }

    /**
     * 请求回调权限回调处理 - 通用
     *
     * @param activity
     */
    private fun onRequestPermissionsResultCommon(activity: Activity) {
        // 获取权限状态
        getPermissionsStatus(activity)
        // 判断请求结果
        requestCallback()
    }

    /**
     * 获取权限状态
     *
     * @param activity
     */
    private fun getPermissionsStatus(activity: Activity) {
        for (permission in mPermissionsRequest) {
            // 判断是否通过请求
            if (isGranted(activity, permission)) {
                mPermissionsGranted.add(permission)
            } else {
                // 未授权
                mPermissionsDenied.add(permission)
                // 拒绝权限
                if (!shouldShowRequestPermissionRationale(activity, permission)) {
                    mPermissionsDeniedForever.add(permission)
                }
            }
        }
    }

    companion object {

        /**
         * Permission 请求Code
         */
        val P_REQUEST_CODE = 100
        /**
         * 全部权限
         */
        private val mAllPermissions = HashSet<String>(1)

        init {
            // 初始化权限数据
            initializePermissionsMap()
        }

        /**
         * 初始化遍历保存全部权限
         */
        @Synchronized
        private fun initializePermissionsMap() {
            val fields = Manifest.permission::class.java.fields
            for (field in fields) {
                var name: String? = null
                try {
                    name = field.get("") as String
                } catch (e: IllegalAccessException) {
                }

                mAllPermissions.add(name!!)
            }
        }

        // ==

        /**
         * 判断是否授予了权限
         *
         * @param permissions
         * @return
         */
        fun isGranted(vararg permissions: String): Boolean {
            // 防止数据为null
            if (permissions != null && permissions.size != 0) {
                // 遍历全部需要申请的权限
                for (permission in permissions) {
                    if (!isGranted(BaseApplication.instance, permission)) {
                        return false
                    }
                }
            }
            return true
        }

        /**
         * 判断是否授予了权限
         *
         * @param context
         * @param permission
         * @return
         */
        private fun isGranted(context: Context, permission: String): Boolean {
            // SDK 版本小于 23 则表示直接通过 || 检查是否通过权限
            return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(context, permission)
        }

        /**
         * 是否拒绝了权限 - 拒绝过一次, 再次申请时, 弹出选择不再提醒并拒绝才会触发 true
         *
         * @param activity
         * @param permission
         * @return
         */
        fun shouldShowRequestPermissionRationale(activity: Activity?, permission: String): Boolean {
            return ActivityCompat.shouldShowRequestPermissionRationale(activity!!, permission)
        }

        // == 使用方法 ==

        /**
         * 申请权限初始化
         *
         * @param permissions
         * @return
         */
        fun permission(vararg permissions: String): PermissionUtils {
            return PermissionUtils(*permissions)
        }

        // == 内部Activity ==

        // 内部持有对象
        private var sInstance: PermissionUtils? = null

        // == 通过传入Activity 方式 ==

        /**
         * 请求权限回调 - 需要在 onRequestPermissionsResult 回调里面调用
         *
         * @param activity
         */
        fun onRequestPermissionsResult(activity: Activity?) {
            if (activity != null && sInstance != null) {
                // 触发回调
                sInstance!!.onRequestPermissionsResultCommon(activity)
            }
        }
    }
}
/**
 * 请求权限
 *
 * @param activity [Fragment.getActivity]
 */