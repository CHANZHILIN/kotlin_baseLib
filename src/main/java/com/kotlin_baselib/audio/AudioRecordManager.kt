package com.kotlin_baselib.audio

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import com.kotlin_baselib.api.Constants
import com.kotlin_baselib.utils.DateUtil
import com.kotlin_baselib.utils.SdCardUtil
import java.io.*
import java.util.*

/**
 *  Created by CHEN on 2019/7/12
 *  Email:1181785848@qq.com
 *  Package:com.kotlin_baselib.audio
 *  Introduce: AudioRecord 录音管理
 **/
class AudioRecordManager {


    private var recordThread: Thread? = null
    var outputStream: DataOutputStream? = null
    private var isRecording: Boolean = false;
    private var audioSource: Int = 0
    private var frequency: Int = 0
    private var channelConfig: Int = 0
    private var audioFormat: Int = 0
    private var recordBufSize: Int = 0
    private var audioRecord: AudioRecord
    private var parent: File

    var mTimer: Timer? = null
    var taskOne: TimerTask? = null
    private var currentRecordMilliSeconds: Long = 0//当前录音的毫秒数

    companion object {
        private var mInstance: AudioRecordManager? = null
        fun getInstance(): AudioRecordManager {
            if (mInstance == null) {
                synchronized(AudioRecordManager::class.java) {
                    if (mInstance == null) {
                        mInstance = AudioRecordManager()
                    }
                }
            }
            return mInstance!!
        }
    }

    init {
        //指定音频源
        audioSource = MediaRecorder.AudioSource.MIC
        //指定采样率(MediaRecoder 的采样率通常是8000Hz CD的通常是44100Hz 不同的Android手机硬件将能够以不同的采样率进行采样。其中11025是一个常见的采样率)
        frequency = 44100
        //指定捕获音频的通道数目.在AudioFormat类中指定用于此的常量
        channelConfig = AudioFormat.CHANNEL_CONFIGURATION_MONO
        //指定音频量化位数 ,在AudioFormaat类中指定了以下各种可能的常量。通常我们选择ENCODING_PCM_16BIT和ENCODING_PCM_8BIT PCM代表的是脉冲编码调制，它实际上是原始音频样本。
        //因此可以设置每个样本的分辨率为16位或者8位，16位将占用更多的空间和处理能力,表示的音频也更加接近真实。
        audioFormat = AudioFormat.ENCODING_PCM_16BIT
        //设置缓存buffer
        recordBufSize = AudioRecord.getMinBufferSize(frequency, channelConfig, audioFormat)
        //构建AudioRecord对象
        audioRecord = AudioRecord(audioSource, frequency, channelConfig, audioFormat, recordBufSize)
        //构建存放音频文件的文件夹
        parent = SdCardUtil.recordDir
    }

    /**
     * 开始录音
     */
    fun startRecord(fileName: String?) {
        try {
            setPath(fileName)
            startRecordThread()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 暂停录音
     */
    fun stopRecord() {
        isRecording = false
    }

    /**
     * 启动录音线程
     */
    private fun startRecordThread() {
        destroyThread()
        isRecording = true
        if (recordThread == null) {
            recordThread = Thread(recordRunnable)
            recordThread!!.start()
        }
    }

    /**
     * 录音线程
     */
    var recordRunnable: Runnable = Runnable {
        try {
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO)

            //构建AudioRecord对象
            audioRecord = AudioRecord(audioSource, frequency, channelConfig, audioFormat, recordBufSize)
            val buffer = ByteArray(recordBufSize)
            //开始录音
            audioRecord.startRecording()
            startTimer() //开始计时
            mListener?.onRecordStart()
            var r = 0
            while (isRecording) {
                val readResult = audioRecord.read(buffer, 0, recordBufSize)
                var sumVolume = 0.0
                for (i in 0 until readResult) {
                    //数据写入文件中
                    outputStream?.write(buffer[i].toInt())
                    sumVolume += Math.abs(buffer[i].toDouble())
                }

                // 平方和除以数据总长度，得到音量大小。
                val avgVolume = sumVolume / readResult
                val volume = 10 * Math.log10(1 + avgVolume)
                mListener?.onVolume(volume)
                r++
                Log.e(Constants.DEBUG_TAG, "pcm录制中...")

            }
            mListener?.onRecordStop()
            //录制完之后，释放AudioRecord
            audioRecord.stop()
            audioRecord.release()
            outputStream?.close()
            currentRecordMilliSeconds = 0
            stopTimer()
            destroyThread()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun setPath(fileName: String?) {
        //生成的文件名
        val file = File(parent, fileName)
        if (file.exists()) {
            file.delete()
        }
        try {
            file.createNewFile()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        outputStream = DataOutputStream(BufferedOutputStream(FileOutputStream(file)))
    }

    /**
     * 销毁线程
     */
    private fun destroyThread() {
        try {
            isRecording = false
            if (null != recordThread && Thread.State.RUNNABLE == recordThread!!.state) {
                try {
                    Thread.sleep(500)
                    recordThread!!.interrupt()
                } catch (e: Exception) {
                    recordThread = null
                }

            }
            recordThread = null
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            recordThread = null
        }
    }

    fun startTimer() {
        mTimer = Timer()
        /**
         * 计时器
         */
        taskOne = object : TimerTask() {

            override fun run() {
                if (isRecording) {
                    currentRecordMilliSeconds += 1000
                    Log.e(Constants.DEBUG_TAG, DateUtil.getFormatHMS(currentRecordMilliSeconds))
                    mListener?.onRecording(DateUtil.getFormatHMS(currentRecordMilliSeconds))
//                runOnUiThread { tv_record_duration.setText(DateUtil.getFormatHMS(currentRecordMilliSeconds)) }
                }
            }
        }
        mTimer?.schedule(taskOne, 0, 1000)
    }

    fun stopTimer() {
        if (mTimer != null) {
            mTimer!!.cancel()
        }
        if (taskOne != null) {
            taskOne!!.cancel()
        }
    }

    var mListener: onRecordStatusChange? = null

    interface onRecordStatusChange {
        fun onRecordStart()
        fun onVolume(volume:Double)
        fun onRecording(time: String)
        fun onRecordStop()
    }

    fun setOnRecordStatusChangeListener(listener: onRecordStatusChange) {
        mListener = listener
    }

}