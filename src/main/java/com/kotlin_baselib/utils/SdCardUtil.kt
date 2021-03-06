package com.kotlin_baselib.utils

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore.Images.ImageColumns
import android.util.Log
import com.kotlin_baselib.api.Constants
import java.io.File
import java.lang.Exception

/**
 *  Created by CHEN on 2019/7/4
 *  Email:1181785848@qq.com
 *  Package:com.kotlin_baselib.utils
 *  Introduce:SDCard
 **/
object SdCardUtil {

    var CACHE_PATH = "" // 应用的cache目录用于存放缓存
    val PROJECT_FILE_PATH =
        Environment.getExternalStorageDirectory().path + File.separator + "deepin_soul" + File.separator // 项目路径
    val DEFAULT_PHOTO_PATH = PROJECT_FILE_PATH + "picture" + File.separator     //图片路径
    val DEFAULT_RECORD_PATH = PROJECT_FILE_PATH + "record" + File.separator     //录音路径
    val DEFAULT_VIDEO_PATH = PROJECT_FILE_PATH + "video" + File.separator       //视频路径

    lateinit var projectDir: File
    lateinit var fileDir: File
    lateinit var recordDir: File
    lateinit var videoDir: File
    lateinit var cacheDir: File


    /**
     * 判断是否有sd
     */
    private fun checkSdState(): Boolean {
        val state = Environment.getExternalStorageState()
        return state == Environment.MEDIA_MOUNTED
    }

    /**
     * 初始化文件目录
     */
    fun initFileDir(context: Context) {
        projectDir = File(PROJECT_FILE_PATH)
        if (!projectDir.exists()) {
            projectDir.mkdirs()
        }
        fileDir = File(DEFAULT_PHOTO_PATH)
        if (!fileDir.exists()) {
            fileDir.mkdirs()
        }
        recordDir = File(DEFAULT_RECORD_PATH)
        if (!recordDir.exists()) {
            recordDir.mkdirs()
        }
        videoDir = File(DEFAULT_VIDEO_PATH)
        if (!videoDir.exists()) {
            videoDir.mkdirs()
        }
        //缓存的目录
        CACHE_PATH = (Environment.getExternalStorageDirectory().path
                + File.separator + "Android" + File.separator + "data" + File.separator + context.packageName + File.separator + "cache" + File.separator)

        cacheDir = File(CACHE_PATH)
        if (!cacheDir.exists()) {
            cacheDir.mkdirs()
        }
    }

    /**
     * 缓存路径
     */
    fun getCachePath(): String {
        return CACHE_PATH
    }

    /**
     * 获取拓展存储Cache的绝对路径
     *
     * @param context
     */
    private fun getExternalCacheDir(context: Context): String? {
        if (!checkSdState())
            return null
        val sb = StringBuilder()
        val file = context.externalCacheDir
        if (file != null) {
            sb.append(file.absolutePath).append(File.separator)
        } else {
            sb.append(Environment.getExternalStorageDirectory().path).append("/Android/data/")
                .append(context.packageName)
                .append("/cache/").append(File.separator).toString()
        }
        return sb.toString()
    }

    /**
     * 获取外部存储路径
     */
    fun getExternalFilesDir(context: Context, type: String): String? {
        if (!checkSdState())
            return null
        val sb = StringBuilder()
        val file = context.getExternalFilesDir(type)
        if (file != null) {
            sb.append(file.absolutePath).append(File.separator)
        } else {
            sb.append(Environment.getExternalStorageDirectory().path).append("/Android/data/")
                .append(context.packageName)
                .append("/files/").append(File.separator).toString()
        }
        return sb.toString()
    }

    /**
     * 获取拍照路径
     *
     * @param context
     * @return
     */
    fun getCameraPath(context: Context): String {
        return getExternalCacheDir(context)!! + "carema.jpg"
    }

    fun getCacheTempImage(context: Context): String {
        return getExternalCacheDir(context) + System.currentTimeMillis() + ".jpg"
    }

    /**
     * Try to return the absolute file path from the given Uri
     *
     * @param context
     * @param uri
     * @return the file path or null
     */
    fun getRealFilePath(context: Context, uri: Uri?): String? {
        if (null == uri)
            return null
        val scheme = uri.scheme
        var data: String? = null
        if (scheme == null)
            data = uri.path
        else if (ContentResolver.SCHEME_FILE == scheme) {
            data = uri.path
        } else if (ContentResolver.SCHEME_CONTENT == scheme) {
            val cursor = context.contentResolver.query(
                uri,
                arrayOf(ImageColumns.DATA), null, null, null
            )
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    val index = cursor.getColumnIndex(ImageColumns.DATA)
                    if (index > -1) {
                        data = cursor.getString(index)
                    }
                }
                cursor.close()
            }
        }
        return data
    }

    /**
     * 获取指定目录下的所有文件名
     */
    fun getFilesAllName(path: String): MutableList<String> {
        val file = File(path)
        val files = file.listFiles()
        val list = ArrayList<String>()
        for (item in files) {
            list.add(item.absolutePath)
        }
        return list
    }

    /**
     * 删除文件
     */
    fun deleteFile(path: String?, callBack: ((success: Boolean) -> Unit)?) {
        val file = File(path)
        if (file.exists()) {
            try {
                file.delete()
                callBack?.invoke(true)
            } catch (e: Exception) {
                callBack?.invoke(false)
                Log.e(Constants.DEBUG_TAG, "failed to delete file：${path}")
            }

        }
    }
}
