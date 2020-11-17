package com.kotlin_baselib.floatview

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.view.View
import android.view.WindowManager
import androidx.annotation.Nullable
import com.alibaba.android.arouter.launcher.ARouter
import com.kotlin_baselib.api.Constants
import com.kotlin_baselib.audio.AudioTrackManager
import com.kotlin_baselib.utils.SdCardUtil
import kotlinx.android.synthetic.main.layout_floating_play_music.view.*
import java.io.File


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
        mFloatPlayMusicView.setOnStatusChangeListener(object :
            FloatingPlayMusicView.onStatusChange {
            override fun onPlay() {
                AudioTrackManager.getInstance()
                    .startPlay(SdCardUtil.recordDir.path + File.separator + fileName)
                AudioTrackManager.getInstance()
                    .setOnAudioStatusChangeListener(object : AudioTrackManager.OnAudioStateChange {
                        override fun onPlay() {
//                        mFloatPlayMusicView.startPlayAnimation()
                        }

                        override fun onStop() {
                            mFloatPlayMusicView.stopPlayAnimation()
                        }
                    })
            }

            override fun onPause() {
                mFloatPlayMusicView.stopPlayAnimation()
                AudioTrackManager.getInstance().stopPlay()
            }

            override fun onEdit() {
//                mFloatPlayMusicView.stopPlayAnimation()
                AudioTrackManager.getInstance().stopPlay()
                mFloatPlayMusicView.removeAllFloatingView() //移除悬浮窗口
                ARouter.getInstance()
                    .build(Constants.EDIT_AUDIO_ACTIVITY_PATH)
                    .withString("fileName", fileName)
                    .navigation()
            }

            override fun onFinishService() {   //关闭service的回调
                stopSelf()
            }
        })
    }


}