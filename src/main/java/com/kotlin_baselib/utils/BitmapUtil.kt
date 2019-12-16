package com.kotlin_baselib.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.media.ExifInterface
import java.io.File
import java.io.FileOutputStream


/**
 *  Created by CHEN on 2019/8/7
 *  Email:1181785848@qq.com
 *  Package:com.kotlin_baselib.utils
 *  Introduce:  图片操作
 **/
object BitmapUtil {


    /**
     * 压缩图片
     */
    fun compressImage(
        srcPath: String,
        desPath: String,
        quality: Int,
        needDeleteSrcPicture: Boolean
    ): String {
        var bitmap = BitmapFactory.decodeFile(srcPath)

        val degree = readPictureDegree(srcPath)//获取相片拍摄角度
        if (degree != 0) {//旋转照片角度，防止头像横着显示
            bitmap = rotateBitmap(bitmap, degree)
        }
        val outputFile = File(desPath)

        val out = FileOutputStream(outputFile)
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)

        if (needDeleteSrcPicture){
            val inputFile = File(srcPath)
            if (inputFile.exists()){
                inputFile.delete()
            }
        }

        return outputFile.getPath()

    }

    /**
     * 获取图片的角度
     */
    fun readPictureDegree(path: String): Int {
        var degree = 0
        val exifInterface = ExifInterface(path)
        val orientation = exifInterface.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> degree = 90
            ExifInterface.ORIENTATION_ROTATE_180 -> degree = 180
            ExifInterface.ORIENTATION_ROTATE_270 -> degree = 270
        }
        return degree
    }

    /**
     * 旋转图片角度
     */
    fun rotateBitmap(bitmap: Bitmap?, degress: Int): Bitmap? {
        var bitmap = bitmap
        if (bitmap != null) {
            val m = Matrix()
            m.postRotate(degress.toFloat())
            bitmap = Bitmap.createBitmap(
                bitmap, 0, 0, bitmap.width,
                bitmap.height, m, true
            )
            return bitmap
        }
        return bitmap
    }


    /**
     * 根据路径获得图片信息并按比例压缩，返回bitmap
     */
    fun getBitmapWithSize(filePath: String,width:Int,height:Int): Bitmap {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true//只解析图片边沿，获取宽高
        BitmapFactory.decodeFile(filePath, options)
        // 计算缩放比
        options.inSampleSize = calculateInSampleSize(options, width, height)
        // 完整解析图片返回bitmap
        options.inJustDecodeBounds = false
        return BitmapFactory.decodeFile(filePath, options)
    }


    /**
     * 计算SampleSize
     */
    fun calculateInSampleSize(
        options: BitmapFactory.Options,
        reqWidth: Int, reqHeight: Int
    ): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            val heightRatio = Math.round(height.toFloat() / reqHeight.toFloat())
            val widthRatio = Math.round(width.toFloat() / reqWidth.toFloat())
            inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
        }
        return inSampleSize
    }

    /**
     * 多张图片横向拼接
     * @param picPaths
     * @return
     */
    fun addHBitmap(bits: List<Bitmap>?): Bitmap? {
        var firstBit: Bitmap? = null
        if (!bits.isNullOrEmpty()) {
            firstBit = bits[0]
            for (i in 1 until bits.size) {
                firstBit = addHBitmap(firstBit!!, bits[i])
            }
        }
        return firstBit

    }


    private fun addHBitmap(first: Bitmap, second: Bitmap): Bitmap {
        val width = first.width + second.width
        val height = Math.max(first.height, second.height)
        val result = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        val canvas = Canvas(result)
        canvas.drawBitmap(first, 0f, 0f, null)
        canvas.drawBitmap(second, first.width.toFloat(), 0f, null)
        return result
    }
}
