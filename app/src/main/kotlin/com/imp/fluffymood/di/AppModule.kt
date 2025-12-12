package com.imp.fluffymood.di

import android.app.Application
import android.content.pm.PackageManager
import android.os.Build
import android.util.Base64
import android.util.Log
import com.imp.fluffymood.BuildConfig
import com.kakao.vectormap.KakaoMapSdk
import dagger.hilt.android.HiltAndroidApp
import java.security.MessageDigest

@HiltAndroidApp
class AppModule: Application() {

    override fun onCreate() {
        super.onCreate()

        initializeService()
    }

    /**
     * 서비스 설정
     */
    private fun initializeService() {

        applicationContext?.let {

            getDebugHashKey()

            /** Initialize Kakao Map Api */
            KakaoMapSdk.init(this, BuildConfig.KAKAO_API_KEY)
        }
    }

    /**
     * Get Debug Hash Key
     */
    private fun getDebugHashKey() {

        if (BuildConfig.DEBUG) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {

                val packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNING_CERTIFICATES)
                for (signature in packageInfo.signingInfo.apkContentsSigners) {

                    try {

                        val messageDigest = MessageDigest.getInstance("SHA")
                        messageDigest.update(signature.toByteArray())
                        Log.d("getKeyHash", "key hash: ${Base64.encodeToString(messageDigest.digest(), Base64.NO_WRAP)}")

                    } catch (e: Exception) {
                        Log.w("getKeyHash", "Unable to get MessageDigest. signature=$signature", e)
                    }
                }
            }
        }
    }
}
