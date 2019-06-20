package com.kotlin_baselib.application

/**
 * Created by CHEN on 2019/6/13
 * Email:1181785848@qq.com
 * Package:com.kotlin_baselib.application
 * Introduce: 所有模块的Application都要把path加入到这里
 */
object ModuleConfig {

    val MODULESLIST: Array<String> = arrayOf(
        "com.soul_login.application.LoginApplication",
        "com.soul_picture.application.PictureApplication",
        "com.soul_music.application.MusicApplication",
        "com.soul_video.application.VideoApplication"
    )
}
