package com.example.musclevision.services

import okhttp3.Interceptor
import okhttp3.Request

class AuthInterceptor(private val accessToken: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val originalRequest: Request = chain.request()
        val requestWithHeaders: Request = originalRequest.newBuilder()
            .header("Authorization", "Bearer $accessToken")
            .build()
        return chain.proceed(requestWithHeaders)
    }
}