package com.example.musclevision.services

import androidx.annotation.StringRes
import com.example.musclevision.data.UploadImageResponse
import com.google.mlkit.vision.pose.PoseLandmark
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import kotlin.math.atan2


interface ApiService {
    @Multipart
    @POST("photo/uploadtogcp")
    suspend fun uploadImage(
        @Part filePart: MultipartBody.Part
    ): Response<UploadImageResponse>
}

fun getAngle(firstPoint: PoseLandmark, midPoint: PoseLandmark, lastPoint: PoseLandmark): Double {
    var result = Math.toDegrees(
        (atan2(lastPoint.getPosition().y - midPoint.getPosition().y,
            lastPoint.getPosition().x - midPoint.getPosition().x)
                - atan2(firstPoint.getPosition().y - midPoint.getPosition().y,
            firstPoint.getPosition().x - midPoint.getPosition().x)).toDouble()
    )
    result = Math.abs(result)
    if (result > 180) {
        result = 360.0 - result // Always get the acute representation of the angle
    }
    return result
}