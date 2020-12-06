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
        const val ITEM_SPACE = 8     //每个item之间的间距
        const val SPAN_COUNT = 3     //瀑布流的列数
        const val LOGIN_ACTIVITY_PATH = "/soul_login/loginActivity"   // 登录页面

        /* picture */
        const val GIF_PICTURE_ACTIVITY_PATH = "/soul_picture/LoadGifActivity"   // 录音页面

        /* music */
        const val RECORD_AUDIO_ACTIVITY_PATH = "/soul_music/recordAudioActivity"   // 录音页面
        const val NRECORD_AUDIO_ACTIVITY_PATH = "/soul_music/nRecordAudioActivity"   // 新录音页面
        const val EDIT_AUDIO_ACTIVITY_PATH = "/soul_music/editAudioActivity"   // 编辑录音页面

        /*video*/
        const val RECORD_VIDEO_ACTIVITY_PATH = "/soul_video/recordVideoActivity"   // 录制视频页面
        const val EDIT_VIDEO_ACTIVITY_PATH = "/soul_video/editVideoActivity"   // 编辑视频页面

        /* login */
        const val FRIENDS_PLANNET_ACTIVITY_PATH = "/soul_login/FriendsPlannetAcctivity"   // 推荐好友球页面
    }
}
