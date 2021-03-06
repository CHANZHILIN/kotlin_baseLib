package com.kotlin_baselib.media.muxer

import android.media.MediaCodec
import android.media.MediaFormat
import android.media.MediaMuxer
import android.os.Environment
import android.util.Log
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.*

/**
 *  Created by CHEN on 2019/10/28
 *  Email:1181785848@qq.com
 *  Introduce:
 **/
class MMuxer {

    private val TAG = "MMuxer"

    private var mPath: String

    private var mMediaMuxer: MediaMuxer? = null

    private var mVideoTrackIndex = -1
    private var mAudioTrackIndex = -1

    private var mIsAudioTrackAdd = false
    private var mIsVideoTrackAdd = false

    private var mIsStart = false

    init {
        val fileName = "LVideo_" + SimpleDateFormat("yyyyMM_dd-HHmmss").format(Date()) + ".mp4"
        val filePath = Environment.getExternalStorageDirectory().absolutePath.toString() + "/"
        mPath = filePath + fileName
        mMediaMuxer = MediaMuxer(mPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
    }

    fun addVideoTrack(mediaFormat: MediaFormat) {
        if (mIsVideoTrackAdd) return
        if (mMediaMuxer != null) {
            mVideoTrackIndex = try {
                mMediaMuxer!!.addTrack(mediaFormat)
            } catch (e: Exception) {
                e.printStackTrace()
                return
            }

            Log.i(TAG, "添加视频轨道")
            mIsVideoTrackAdd = true
            startMuxer()
        }
    }

    fun addAudioTrack(mediaFormat: MediaFormat) {
        if (mIsAudioTrackAdd) return
        if (mMediaMuxer != null) {
            mAudioTrackIndex = try {
                mMediaMuxer!!.addTrack(mediaFormat)
            } catch (e: Exception) {
                e.printStackTrace()
                return
            }
            Log.i(TAG, "添加音频轨道")
            mIsAudioTrackAdd = true
            startMuxer()
        }
    }

    fun setNoAudio() {
        if (mIsAudioTrackAdd) return
        mIsAudioTrackAdd = true
        startMuxer()
    }

    fun setNoVideo() {
        if (mIsVideoTrackAdd) return
        mIsVideoTrackAdd = true
        startMuxer()
    }

    fun writeVideoData(byteBuffer: ByteBuffer, bufferInfo: MediaCodec.BufferInfo) {
        if (mIsStart) {
            mMediaMuxer?.writeSampleData(mVideoTrackIndex, byteBuffer, bufferInfo)
        }
    }

    fun writeAudioData(byteBuffer: ByteBuffer, bufferInfo: MediaCodec.BufferInfo) {
        if (mIsStart) {
            mMediaMuxer?.writeSampleData(mAudioTrackIndex, byteBuffer, bufferInfo)
        }
    }

    private fun startMuxer() {
        if (mIsAudioTrackAdd && mIsVideoTrackAdd) {
            mMediaMuxer?.start()
            mIsStart = true
            Log.i(TAG, "启动封装器")
        }
    }

    fun release() {
        mIsAudioTrackAdd = false
        mIsVideoTrackAdd = false
        try {
            mMediaMuxer?.stop()
            mMediaMuxer?.release()
            mMediaMuxer = null
            Log.i(TAG, "退出封装器")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}