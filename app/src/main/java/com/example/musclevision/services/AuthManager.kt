package com.example.musclevision.services

import android.util.Log
import com.example.musclevision.data.TokenDto
import com.example.musclevision.data.TokenRequestDto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object AuthManager {

    var accessToken: String? = null
    var refreshToken: String? = null

    fun refreshAccessToken(onTokenRefreshed: (Boolean) -> Unit) {
        if (refreshToken == null) {
            Log.e("AuthManager", "Refresh token is null. Cannot refresh access token.")
            onTokenRefreshed(false)
            return
        }

        val request = TokenRequestDto(accessToken.orEmpty(), refreshToken.orEmpty())
        RetrofitClient.instance.reissue(request).enqueue(object : Callback<TokenDto> {
            override fun onResponse(call: Call<TokenDto>, response: Response<TokenDto>) {
                if (response.isSuccessful) {
                    val tokenResponse = response.body()
                    accessToken = tokenResponse?.accessToken
                    refreshToken = tokenResponse?.refreshToken
                    onTokenRefreshed(true)
                } else {
                    Log.e("AuthManager", "Token refresh failed: ${response.errorBody()?.string()}")
                    onTokenRefreshed(false)
                }
            }

            override fun onFailure(call: Call<TokenDto>, t: Throwable) {
                Log.e("AuthManager", "Token refresh error", t)
                onTokenRefreshed(false)
            }
        })
    }
}