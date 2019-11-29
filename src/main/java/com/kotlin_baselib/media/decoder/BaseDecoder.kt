package com.kotlin_baselib.media.decoder

import android.media.MediaCodec
import android.media.MediaFormat
import android.util.Log
import com.kotlin_baselib.api.Constants
import com.kotlin_baselib.media.extractor.IExtractor
import java.io.File
import java.nio.ByteBuffer

/**
 *  Created by CHEN on 2019/10/22
 *  Email:1181785848@qq.com
 *  Introduce:
 **/
abstract class BaseDecoder(private val mFilePath: String) : IDecoder {

    /**
     * 解码器是否在运行
     */
    private var mIsRunning = true

    /*
        解码状态
     */
    private var mState = DecodeState.STOP
    protected var mStateListener: IDecoderStateListener? = null

    /**
     * 音视频数据读取器
     */
    private var mExtractor: IExtractor? = null

    private var mDuration: Long = 0

    private var mStartPos: Long = 0

    private var mEndPos: Long = 0


    protected var mVideoWidth = 0

    protected var mVideoHeight = 0

    /**
     * 音视频解码器
     */
    private var mCodec: MediaCodec? = null

    /**
     * 解码输入缓存区
     */
    private var mInputBuffers: Array<ByteBuffer>? = null

    /**
     * 解码输出缓存区
     */
    private var mOutputBuffers: Array<ByteBuffer>? = null

    /**
     * 解码数据信息
     */
    private var mBufferInfo = MediaCodec.BufferInfo()

    /**
     * 开始解码时间，用于音视频同步
     */
    private var mStartTimeForSync = -1L

    /**
     * 流数据是否结束
     */
    private var mIsEOS = false

    private var mTimeStamp = 0L

    private var isAutoPlay = true

    /**
     * 线程等待锁
     */
    private val mLock = Object()

    override fun run() {
        if (mState == DecodeState.STOP)
            mState = DecodeState.START
        mStateListener?.decoderPrepare(this)

        //【解码步骤：1. 初始化，并启动解码器】
        if (!init()) return
        Log.d(Constants.DEBUG_TAG, "${mExtractor!!.getFormat()!!.getString(MediaFormat.KEY_MIME).split("/")[0]}开始解码")
        try {
            while (mIsRunning) {

                if (mState != DecodeState.START && mState != DecodeState.DECODING && mState != DecodeState.SEEKING) {
                    waitDecode()

                    // ---------【同步时间矫正】-------------
                    //恢复同步的起始时间，即去除等待流失的时间
                    mStartTimeForSync = System.currentTimeMillis() - getCurTimeStamp()
                }


                if (mState == DecodeState.STOP) {
                    mIsRunning = false
                    break
                }

                if (mStartTimeForSync == -1L) {
                    mStartTimeForSync = System.currentTimeMillis()
                }
                //如果数据没有解码完毕或者不处于seeking状态（等待当前帧解码完成再重新压入数据），将数据推入解码器解码
                if (!mIsEOS && mState != DecodeState.SEEKING) {
                    //【解码步骤：2. 将数据压入解码器输入缓冲】
                    mIsEOS = pushBufferToDecoder()
                }

                //【解码步骤：3. 将解码好的数据从缓冲区拉取出来】
                val index = pullBufferFromDecoder()
                if (index > 0) {    //缓冲区有数据
                    // ---------【音视频同步】-------------
                    if (mState == DecodeState.DECODING) {
                        sleepRender()
                    }
                    if (mState != DecodeState.SEEKING) {
                        //【解码步骤：4. 渲染】
                        render(mOutputBuffers!![index], mBufferInfo)
                        //【解码步骤：5. 释放输出缓冲】
                        mCodec!!.releaseOutputBuffer(index, true)
                        if (mState == DecodeState.START) {
                            mState = DecodeState.PAUSE
                        }

                        //【解码步骤：6. 判断解码是否完成】
                        if (mBufferInfo.flags == MediaCodec.BUFFER_FLAG_END_OF_STREAM) {
                            Log.d(Constants.DEBUG_TAG, "${mExtractor!!.getFormat()!!.getString(MediaFormat.KEY_MIME).split("/")[0]}解码结束")
                            mState = DecodeState.FINISH
                            mStateListener?.decoderFinish(this)
                        }
                    }
                } else if (index <= 0 && mState == DecodeState.SEEKING) {
                    //缓冲区无数据
                    mExtractor!!.seek(mTimeStamp)
                    mCodec!!.flush()    //在flush()前需要等待当前帧缓冲区解码完成，否则会出现java.lang.IllegalStateException: buffer is inaccessible错误
//                    mState = DecodeState.START
                    mStartTimeForSync = -1L
                    if (isAutoPlay) goOnDecode()
                }


            }
        } catch (e: Exception) {
            Log.e(Constants.DEBUG_TAG, "解码失败：${e}")
        } finally {
            doneDecode()
            release()
        }
    }

    /**
     * 1. 初始化，并启动解码器
     */
    private fun init(): Boolean {
        if (mFilePath.isEmpty() || !File(mFilePath).exists()) {
            Log.e(Constants.DEBUG_TAG, "文件路径不存在")
            mStateListener?.decoderError(this, "文件路径不存在")
            return false
        }
        if (!check()) return false
        //初始化数据提取器
        mExtractor = initExtractor(mFilePath)
        if (mExtractor == null || mExtractor!!.getFormat() == null) {
            Log.e(Constants.DEBUG_TAG, "无法解析文件")
            return false
        }

        //初始化参数
        if (!initParams()) return false

        //初始化渲染器
        if (!initRender()) return false

        //初始化解码器
        if (!initCodec()) return false
        return true
    }

    private fun initParams(): Boolean {
        try {
            val format = mExtractor!!.getFormat()!!
            mDuration = format.getLong(MediaFormat.KEY_DURATION) / 1000
            if (mEndPos == 0L) mEndPos = mDuration

            initSpecParams(mExtractor!!.getFormat()!!)
        } catch (e: Exception) {
            return false
        }
        return true
    }

    private fun initCodec(): Boolean {
        try {
            val type = mExtractor!!.getFormat()!!.getString(MediaFormat.KEY_MIME)
            mCodec = MediaCodec.createDecoderByType(type)//查询MediaFormat中的编码类型（如video/avc，即H264；audio/mp4a-latm，即AAC）；
            if (!configCodec(mCodec!!, mExtractor!!.getFormat()!!)) {
                waitDecode()
            }
            mCodec!!.start()

            mInputBuffers = mCodec?.inputBuffers
            mOutputBuffers = mCodec?.outputBuffers
        } catch (e: Exception) {
            return false
        }
        return true
    }

    /**
     * 解码线程进入等待
     */
    private fun waitDecode() {
        try {
            if (mState == DecodeState.PAUSE) {
                mStateListener?.decoderPause(this)
            }
            synchronized(mLock) {
                mLock.wait()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    /**
     * 将数据压入解码器输入缓冲
     */
    private fun pushBufferToDecoder(): Boolean {
        val inputBufferIndex = mCodec!!.dequeueInputBuffer(1000)
        var isEndOfStream = false

        if (inputBufferIndex >= 0) {
            val inputBuffer = mInputBuffers!![inputBufferIndex]
            val sampleSize = mExtractor!!.readBuffer(inputBuffer)
            if (sampleSize < 0) {
                //如果数据已经取完，压入数据结束标志：MediaCodec.BUFFER_FLAG_END_OF_STREAM
                mCodec!!.queueInputBuffer(inputBufferIndex, 0, 0,
                        0, MediaCodec.BUFFER_FLAG_END_OF_STREAM)
                isEndOfStream = true
            } else {
                mCodec!!.queueInputBuffer(inputBufferIndex, 0,
                        sampleSize, mExtractor!!.getCurrentTimestamp(), 0)

            }
        }
        return isEndOfStream
    }

    /**
     * 将解码好的数据从缓冲区取出
     */
    private fun pullBufferFromDecoder(): Int {
        // 查询是否有解码完成的数据，index >=0 时，表示数据有效，并且index为缓冲区索引
        val index = mCodec!!.dequeueOutputBuffer(mBufferInfo, 1000)
        when (index) {
            MediaCodec.INFO_OUTPUT_FORMAT_CHANGED -> {
            }
            MediaCodec.INFO_TRY_AGAIN_LATER -> {
            }
            MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED -> {
                mOutputBuffers = mCodec!!.outputBuffers
            }
            else -> {
                return index
            }
        }
        return -1
    }

    private fun sleepRender() {
        val passTime = System.currentTimeMillis() - mStartTimeForSync
        val curTime = getCurTimeStamp()
        if (curTime > passTime) {
            Thread.sleep(curTime - passTime)
        }
    }

    private fun release() {
        try {
            Log.d(Constants.DEBUG_TAG, "解码停止，释放解码器")
            mState = DecodeState.STOP
            mIsEOS = true
            mExtractor?.stop()
            mCodec?.stop()
            mCodec?.release()
            mStateListener?.decoderDestroy(this)
        } catch (e: Exception) {
        }
    }


    /**
     * 检查子类参数
     */
    abstract fun check(): Boolean

    /**
     * 初始化数据提取器
     */
    abstract fun initExtractor(path: String): IExtractor

    /**
     * 初始化子类自己特有的参数
     */
    abstract fun initSpecParams(format: MediaFormat)

    /**
     * 初始化渲染器
     */
    abstract fun initRender(): Boolean

    /**
     * 配置解码器
     */
    abstract fun configCodec(codec: MediaCodec, format: MediaFormat): Boolean

    /**
     * 渲染
     */
    abstract fun render(outputBuffer: ByteBuffer,
                        bufferInfo: MediaCodec.BufferInfo)


    /**
     * 结束解码
     */
    abstract fun doneDecode()

    /**
     * 通知解码线程继续运行
     */
    protected fun notifyDecode() {
        synchronized(mLock) {
            mLock.notifyAll()
        }
        if (mState == DecodeState.DECODING) {
            mStateListener?.decoderRunning(this)
        }
    }

    override fun pause() {
        mState = DecodeState.PAUSE
    }

    override fun goOnDecode() {
        mState = DecodeState.DECODING
        notifyDecode()
    }

    /**
     * 跳转到指定位置
     */
    override fun seekTo(pos: Long): Long {
        goOnDecode()    //继续进行解码
        isAutoPlay = false
        mState = DecodeState.SEEKING
        mIsRunning = true
        mTimeStamp = pos
        mIsEOS = false
        return mTimeStamp
    }
    /**
     * 跳转到指定位置，并播放
     */
    override fun seekAndPlay(pos: Long): Long {
        goOnDecode()    //继续进行解码
        isAutoPlay = true
        mState = DecodeState.SEEKING
        mIsRunning = true
        mTimeStamp = pos
        mIsEOS = false
        return mTimeStamp
    }



    override fun stop() {
        mState = DecodeState.STOP
        mIsRunning = false
    }

    override fun isDecoding(): Boolean {
        return mState == DecodeState.DECODING
    }

    override fun isSeeking(): Boolean {
        return mState == DecodeState.SEEKING
    }

    override fun isStop(): Boolean {
        return mState == DecodeState.STOP
    }

    override fun setSizeListener(l: IDecoderProgress) {
    }


    override fun getWidth(): Int {
        return mVideoWidth
    }

    override fun getHeight(): Int {
        return mVideoHeight
    }

    override fun getDuration(): Long {
        return mDuration
    }


    override fun getRotationAngle(): Int {
        return 0
    }

    override fun getMediaFormat(): MediaFormat? {
        return mExtractor?.getFormat()
    }

    override fun getTrack(): Int {
        return 0
    }

    override fun getFilePath(): String {
        return mFilePath
    }

    override fun asCropper(): IDecoder {
        return this
    }

    /**
     * 设置状态监听
     */
    override fun setStateListener(l: IDecoderStateListener?) {
        mStateListener = l
    }

    override fun getCurTimeStamp(): Long {
        return mBufferInfo.presentationTimeUs / 1000
    }

}