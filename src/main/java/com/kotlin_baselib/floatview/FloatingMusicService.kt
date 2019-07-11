package com.kotlin_baselib.floatview

import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.AudioTrack
import android.media.MediaRecorder
import android.os.IBinder
import android.support.annotation.Nullable
import android.view.View
import android.view.WindowManager
import com.kotlin_baselib.utils.SdCardUtil
import kotlinx.android.synthetic.main.layout_floating_play_music.view.*
import java.io.*
import kotlin.concurrent.thread


/**
 *  Created by CHEN on 2019/7/5
 *  Email:1181785848@qq.com
 *  Package:com.kotlin_baselib.floatview
 *  Introduce:  播放音乐的服务
 **/
class FloatingMusicService : Service() {
    companion object {
        var isStarted = false
        lateinit var windowManager: WindowManager
        var mWidth: Int = 0
        var mHeight: Int = 0
        lateinit var mFloatDeleteView: FloatingDeleteView
    }

    private lateinit var mFloatPlayMusicView: FloatingPlayMusicView

    var fileName: String? = null

    private var audioSource: Int = 0
    private var frequency: Int = 0
    private var channelConfig: Int = 0
    private var audioFormat: Int = 0
    private var recordBufSize: Int = 0
    private lateinit var audioRecord: AudioRecord
    private lateinit var parent: File
    private lateinit var mAudioTrack: AudioTrack
    var dis: DataInputStream? = null  //数据输入流


    override fun onCreate() {
        super.onCreate()
        isStarted = true
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        mWidth = windowManager.defaultDisplay.width      //屏幕宽度
        mHeight = windowManager.defaultDisplay.height    //屏幕高度
    }


    @Nullable
    override fun onBind(intent: Intent): IBinder? {
        return null
    }


    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        fileName = intent.getStringExtra("fileName")
        initAudio()
        showFloatingDeleteWindow()
        mFloatDeleteView.visibility = View.GONE
        showFloatingWindow()

        return super.onStartCommand(intent, flags, startId)
    }

    /**
     * 显示删除区域
     */
    private fun showFloatingDeleteWindow() {
        mFloatDeleteView = FloatingDeleteView(applicationContext)
    }

    /**
     * 显示播放音频区域
     */
    private fun showFloatingWindow() {
        mFloatPlayMusicView = FloatingPlayMusicView(applicationContext)
        mFloatPlayMusicView.tv_audio_name.text = fileName
        mFloatPlayMusicView.setOnStatusChangeListener(object : FloatingPlayMusicView.onStatusChange {
            override fun onPlay() {
                playPcm(fileName)
            }

            override fun onPause() {
                stopPlayPcm()
            }

            override fun onFinishService() {   //关闭service的回调
                stopSelf()
            }
        })
    }

    /**
     * 初始化和配置AudioRecord
     */
    private fun initAudio() {
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
     * 播放pcm格式的音频文件
     */
    private fun playPcm(fileName: String?) {
        thread(start = true) {
            val file = File(parent, fileName)
            try {
                //获取到录音文件输入流
                dis = DataInputStream(BufferedInputStream(FileInputStream(file)))
                //获取缓存大小
                val bufferSize = AudioTrack.getMinBufferSize(frequency, channelConfig, audioFormat)
                //构建AudioTrack
                mAudioTrack = AudioTrack(audioSource, frequency, channelConfig, audioFormat, bufferSize, AudioTrack.MODE_STREAM)
                //开辟内存空间
                val datas = ByteArray(bufferSize)
                //开始播放
                mAudioTrack.play()
                while (true) {
                    var i = 0
                    try {
                        while (dis!!.available() > 0 && i < datas.size) {
                            datas[i] = dis!!.readByte()
                            i++
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                    mAudioTrack.write(datas, 0, datas.size)
                    //表示读取完了
                    if (i != bufferSize) {
                        //释放AudioTrack
                        mAudioTrack.stop()
                        mAudioTrack.release()
                        break
                    }
                }
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
        }

    }

    /**
     * 停止播放
     */
    fun stopPlayPcm() {
        try {

            if (mAudioTrack.state === AudioRecord.STATE_INITIALIZED) {//初始化成功
                mAudioTrack.stop()//停止播放
            }
            mAudioTrack.release()//释放audioTrack资源
            dis?.close()//关闭数据输入流
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


}