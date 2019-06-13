package com.kotlin_baselib.api

import android.util.Log
import com.google.gson.Gson
import okhttp3.*
import okhttp3.internal.Util.UTF_8
import okhttp3.logging.HttpLoggingInterceptor
import okio.Buffer
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.nio.charset.Charset
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by CHEN on 2019/6/13
 * Email:1181785848@qq.com
 * Package:com.kotlin_baselib.api
 * Introduce:请求网络，拦截器
 */
class ApiEngine private constructor() {
    private val retrofit: Retrofit

    val apiService: Api
        get() = retrofit.create(Api::class.java)


    init {
        //日志拦截器
        val interceptor = object : Interceptor {
            @Throws(IOException::class)
            override fun intercept(chain: Interceptor.Chain): Response {
                val request = chain.request()
                printRequestMessage(request)
                val response = chain.proceed(request)
                printResponseMessage(response)
                return response
            }

            /** * 打印请求消息 * * @param request 请求的对象  */
            private fun printRequestMessage(request: Request?) {

                if (request == null) {
                    return
                }

                Log.e(
                    Constants.DEBUG_TAG,
                    "==================================请求信息 start ===================================" + "\n"
                            + "Url : " + request.url().url().toString() + "\n"
                            + "Method: " + request.method() + "\n"
                            + "Heads : " + request.headers()
                )

                val requestBody = request.body() ?: return
                try {
                    val bufferedSink = Buffer()
                    requestBody.writeTo(bufferedSink)
                    val mediaType = requestBody.contentType()
                    if (mediaType != null) {
                        var charset = mediaType.charset()
                        charset = charset ?: Charset.forName("utf-8")
                        Log.e(Constants.DEBUG_TAG, "Params: " + bufferedSink.readString(charset!!))
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                } catch (e: NullPointerException) {
                    e.printStackTrace()
                }

                Log.e(
                    Constants.DEBUG_TAG,
                    "==================================请求信息  end  ==================================="
                )
            }

            /**
             *
             * 打印返回消息
             * @param response 返回的对象
             */
            private fun printResponseMessage(response: Response?) {
                if (response == null || !response.isSuccessful) {
                    return
                }
                val responseBody = response.body()
                val contentLength = responseBody!!.contentLength()
                val source = responseBody.source()
                try {
                    source.request(java.lang.Long.MAX_VALUE) // Buffer the entire body.
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                val buffer = source.buffer()
                var charset: Charset? = UTF_8
                val contentType = responseBody.contentType()
                if (contentType != null) {
                    charset = contentType.charset()
                }
                if (contentLength != 0L) {
                    val result = buffer.clone().readString(charset!!)
                    Log.e(
                        Constants.DEBUG_TAG,
                        "==================================返回消息 start ===================================" + "\n"
                                + "Response: " + result + "\n"
                                + "==================================返回消息  end  ==================================="
                    )
                }
            }

        }


        val cookieJar = object : CookieJar {
            private val cookieStore = HashMap<String, List<Cookie>>()

            //Tip：這裡key必須是String
            override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
                cookieStore[url.host()] = cookies
            }

            override fun loadForRequest(url: HttpUrl): List<Cookie> {
                val cookies = cookieStore[url.host()]
                return cookies ?: ArrayList()
            }
        }

        val builder = OkHttpClient.Builder()
            .connectTimeout(12, TimeUnit.SECONDS)
            .readTimeout(12, TimeUnit.SECONDS)
            .writeTimeout(12, TimeUnit.SECONDS)
            .cookieJar(cookieJar)
            .addInterceptor(interceptor)

        // 上线时，修改下面注释
        //当且仅当构建模式是debug的时候，才打印输出请求body信息
        if (Constants.DEBUG) {
            builder.addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        }
        val client = builder.build()

        retrofit = Retrofit.Builder()
            .baseUrl(Api.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(Gson()))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()

    }

    companion object {
        @Volatile
        private var apiEngine: ApiEngine? = null

        val instance: ApiEngine?
            get() {
                if (apiEngine == null) {
                    synchronized(ApiEngine::class.java) {
                        if (apiEngine == null) {
                            apiEngine = ApiEngine()
                        }
                    }
                }
                return apiEngine
            }

    }
}
