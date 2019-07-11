package com.kotlin_baselib.api

/**
 * Created by CHEN on 2019/6/13
 * Email:1181785848@qq.com
 * Package:com.kotlin_baselib.api
 * Introduce:
 */
interface Constants {
    companion object {
        const val DEBUG_TAG = "CHEN"
        const val DEBUG = true
        const val LOGIN_ACTIVITY_PATH = "/soul_login/loginActivity"   // 登录页面

        /* music */
        const val RECORD_AUDIO_ACTIVITY_PATH = "/soul_music/recordAudioActivity"   // 录音页面
        /* login */
        const val FRIENDS_PLANNET_ACTIVITY_PATH = "/soul_login/FriendsPlannetAcctivity"   // 推荐好友球页面
    }
}
