package com.kotlin_baselib.audio

import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioRecord
import android.media.AudioTrack
import java.io.DataInputStream
import java.io.File
import java.io.FileInputStream


/**
 *  Created by CHEN on 2019/7/12
 *  Email:1181785848@qq.com
 *  Package:com.soul_music.utils
 *  Introduce:  AudioTrack封装
 **/
class AudioTrackManager {

    private var audioTrack: AudioTrack? = null
    private var dis: DataInputStream? = null
    private var audioTrackThread: Thread? = null
    private var isStart = false

    private var bufferSize: Int

    private var audioSource: Int
    private var frequency: Int
    private var channelConfig: Int
    private var audioFormat: Int

    companion object {
        private var mInstance: AudioTrackManager? = null
        fun getInstance(): AudioTrackManager {
            if (mInstance == null) {
                synchronized(AudioTrackManager::class.java) {
                    if (mInstance == null) {
                        mInstance = AudioTrackManager()
                    }
                }
            }
            return mInstance!!
        }
    }

    init {
        //指定音频源
        audioSource = AudioManager.STREAM_MUSIC
        //指定采样率(MediaRecoder 的采样率通常是8000Hz CD的通常是44100Hz 不同的Android手机硬件将能够以不同的采样率进行采样。其中11025是一个常见的采样率)
        //设置音频采样率，44100是目前的标准，但是某些设备仍然支持22050，16000，11025
        frequency = 16000
        //指定捕获音频的通道数目.在AudioFormat类中指定用于此的常量
        channelConfig = AudioFormat.CHANNEL_CONFIGURATION_MONO
        //指定音频量化位数 ,在AudioFormat类中指定了以下各种可能的常量。通常我们选择ENCODING_PCM_16BIT和ENCODING_PCM_8BIT PCM代表的是脉冲编码调制，它实际上是原始音频样本。
        //因此可以设置每个样本的分辨率为16位或者8位，16位将占用更多的空间和处理能力,表示的音频也更加接近真实。
        audioFormat = AudioFormat.ENCODING_PCM_16BIT
        bufferSize = AudioTrack.getMinBufferSize(frequency, channelConfig, audioFormat)
    }


    /**
     * 开始播放
     */
    fun startPlay(path: String?) {
        try {
            setPath(path)
            startThread()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 停止播放
     */
    fun stopPlay() {
        try {
            destroyThread()
            if (audioTrack?.state == AudioRecord.STATE_INITIALIZED) {
                audioTrack?.stop()
                mListener?.onStop()
            }
            audioTrack?.release()
            if (dis != null) {
                dis!!.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun isPlaying(): Boolean {
        return audioTrack?.playState == AudioTrack.PLAYSTATE_PLAYING
    }


    /**
     * 销毁线程
     */
    private fun destroyThread() {
        try {
            isStart = false
            if (null != audioTrackThread && Thread.State.RUNNABLE == audioTrackThread!!.state) {
                try {
                    Thread.sleep(500)
                    audioTrackThread!!.interrupt()
                } catch (e: Exception) {
                    audioTrackThread = null
                }

            }
            audioTrackThread = null
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            audioTrackThread = null
        }
    }

    /**
     * 启动播放线程
     */
    private fun startThread() {
        destroyThread()
        isStart = true
        if (audioTrackThread == null) {
            audioTrackThread = Thread(audioTrackRunnable)
            audioTrackThread!!.start()
        }
    }

    /**
     * 播放线程
     */
    var audioTrackRunnable: Runnable = Runnable {
        try {
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO)
            audioTrack = AudioTrack(audioSource, frequency, channelConfig, audioFormat, bufferSize, AudioTrack.MODE_STREAM)

            val tempBuffer = ByteArray(bufferSize)
            var readCount = 0
            while (dis!!.available() > 0) {
                readCount = dis!!.read(tempBuffer)
                if (readCount == AudioTrack.ERROR_INVALID_OPERATION || readCount == AudioTrack.ERROR_BAD_VALUE) {
                    continue
                }
                if (readCount != 0 && readCount != -1) {
                    audioTrack?.play()
                    mListener?.onPlay()
                    audioTrack?.write(tempBuffer, 0, readCount)
                }
            }
            stopPlay()
            mListener?.onStop()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 设置文件路径
     */
    private fun setPath(path: String?) {
        val file = File(path)
        dis = DataInputStream(FileInputStream(file))
    }


    var mListener: OnAudioStateChange? = null

    interface OnAudioStateChange {
        fun onPlay()
        fun onStop()
    }

    fun setOnAudioStatusChangeListener(listener: OnAudioStateChange) {
        mListener = listener
    }



}