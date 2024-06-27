package com.example.musclevision.services

import android.content.Context
import android.net.Uri
import com.example.musclevision.data.LoginRequestDto
import com.example.musclevision.data.LoginResponseDto
import com.example.musclevision.data.TokenDto
import com.example.musclevision.data.TokenRequestDto
import com.example.musclevision.data.UploadImageResponse
import com.example.musclevision.services.AuthManager.accessToken
import com.google.gson.GsonBuilder
import com.google.mlkit.vision.pose.PoseLandmark
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import kotlin.math.atan2


interface ApiService {
    @Multipart
    @POST("photo/uploadtogcp")
    suspend fun uploadImage(
        @Part image: MultipartBody.Part
    ): Response<UploadImageResponse>

    @POST("photo/sendresult")
    suspend fun getResult(
        @Body imageUri: String
    ): Response<UploadImageResponse>

    @POST("auth/google")
    fun sendTokenToServer(@Body idToken: String?): Call<TokenDto>

    @POST("/auth/signup")
    fun signup(@Body request: LoginRequestDto): Call<LoginResponseDto>

    @POST("/auth/login")
    fun login(@Body request: LoginRequestDto): Call<TokenDto>

    @POST("/auth/reissue")
    fun reissue(@Body request: TokenRequestDto) : Call<TokenDto>

    @POST("/auth/emailAuthentication")
    fun emailAuth(@Body email: String) : Call<String>
}


fun getAngle(leftLandmark: PoseLandmark, rightLandmark: PoseLandmark): Float {

    val x1 = leftLandmark.position.x
    val y1 = leftLandmark.position.y

    val x2 = rightLandmark.position.x
    val y2 = rightLandmark.position.y

    val deltaX = x1 - x2
    val deltaY = y2 - y1

    val angleRadians = atan2(deltaY, deltaX)

    val angleDegrees = Math.toDegrees(angleRadians.toDouble()).toFloat()

    return angleDegrees
}
fun getDescription(angleDegrees : Float) : Pair<Float,String>{

    val description = when {
        angleDegrees in -3.0..3.0 -> "정상"
        angleDegrees in -8.0..8.0 -> "약간의 불균형"
        angleDegrees < -8.0 || angleDegrees > 8.0 -> "심한 불균형"
        else -> "Unknown angle range"
    }

    val unbalanceInformation : Pair<Float, String> = Pair(angleDegrees, description)
    return unbalanceInformation
}

suspend fun uploadImage(imageFile: File): Response<UploadImageResponse> {
    val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor(accessToken!!))
        .build()

    val retrofit = Retrofit.Builder()
        .baseUrl("http://35.216.89.227:9090/")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
        .build()

    val service = retrofit.create(ApiService::class.java)

    val requestFile = RequestBody.create(MediaType.parse("image/jpeg"), imageFile)
    val body = MultipartBody.Part.createFormData("image", imageFile.name, requestFile)
    return service.uploadImage(body)
}

fun uriToFile(uri: Uri, context: Context): File? {
    val contentResolver = context.contentResolver
    val fileName = "temp_file_" + System.currentTimeMillis()
    val tempFile = File(context.cacheDir, fileName)

    try {
        val inputStream: InputStream? = contentResolver.openInputStream(uri)
        val outputStream: OutputStream = FileOutputStream(tempFile)

        inputStream?.use { input ->
            outputStream.use { output ->
                val buffer = ByteArray(4 * 1024)
                var read: Int
                while (input.read(buffer).also { read = it } != -1) {
                    output.write(buffer, 0, read)
                }
                output.flush()
            }
        }
        return tempFile
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }
}