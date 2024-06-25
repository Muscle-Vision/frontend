package com.example.musclevision.ui.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.media.ExifInterface
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil.compose.rememberImagePainter
import com.example.musclevision.R
import com.example.musclevision.data.UnbalanceFigureDto
import com.example.musclevision.data.UploadImageResponse
import com.example.musclevision.services.ApiService
import com.example.musclevision.services.AuthInterceptor
import com.example.musclevision.services.AuthManager
import com.example.musclevision.services.AuthManager.accessToken
import com.example.musclevision.services.getAngle
import com.example.musclevision.services.getDescription
import com.example.musclevision.services.uploadImage
import com.example.musclevision.services.uriToFile
import com.google.gson.GsonBuilder
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.PoseLandmark
import com.google.mlkit.vision.pose.accurate.AccuratePoseDetectorOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException


@Composable
fun ReportScreen(
    onNextButtonClicked: ()-> Unit,
    imageUri : Uri,
    modifier: Modifier = Modifier,
    receivedUri: String
){
    var analyzedImageUri by remember { mutableStateOf<Uri?>(null) }
    var analyzedFigures by remember { mutableStateOf<UnbalanceFigureDto?>(null) }
    val context = LocalContext.current
    // `remember`를 사용하여 `file`을 저장
    val file by remember(imageUri) {
        mutableStateOf(uriToFile(imageUri, context))
    }

    var score by remember { mutableStateOf<String?>("") }

    val updatedAnalyzedImageUri by rememberUpdatedState(newValue = analyzedImageUri)


    Log.d("ReportScreenGetScore",receivedUri)

    // `file`이 null이 아닌 경우에만 `LaunchedEffect` 실행
    LaunchedEffect(file) {
        if (file != null && updatedAnalyzedImageUri == null) {
            detectPose(file!!, context) { uri, description ->
                analyzedImageUri = uri
                analyzedFigures = description
                Log.d("ReportScreen","$uri, $description")
            }
        }
        try {
            val response = withContext(Dispatchers.IO) { getScore(receivedUri) }
            Log.d("ReportScreenGetScore","응답으로 받은것: $response")
            if (response.isSuccessful) {
                // 업로드 성공
                score = response.body()?.result
                // 서버 응답에 따라 적절한 처리를 수행합니다.
                Log.d("ReportScreenGetScore","성공 : ${response.body()?.photoRoute}")
                Log.d("ReportScreenGetScore",score!!)

            } else {
                // 업로드 실패
                val errorMessage = response.message()

                // 오류 처리
                Log.d("ReportScreenGetScore","에러로 받은것: $errorMessage")
            }
        } catch (e: Exception) {
            // 예외 처리
            Log.e("ReportScreenGetScore", "Error get score: ${e.message}")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("나의 점수 :$score")
        if (analyzedImageUri != null) {
            val painter: Painter = rememberImagePainter(
                data = analyzedImageUri,
                builder = {
                    crossfade(true)
                    error(R.drawable.ic_google_logo) // 에러 시 표시할 이미지
                }
            )
            Box(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .fillMaxWidth()
                    .height(400.dp)
            ) {
                Image(
                    painter = painter,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
            }
        } else {
            Text("이미지를 처리 중입니다...")
        }
    }
}


fun detectPose(
    imageFile: File,
    context: Context,
    onImageProcessed: (Uri, UnbalanceFigureDto) -> Unit,
){
    val poseDetectorOptions = AccuratePoseDetectorOptions.Builder()
        .setDetectorMode(AccuratePoseDetectorOptions.SINGLE_IMAGE_MODE)
        .build()

    val poseDetector = PoseDetection.getClient(poseDetectorOptions)

    val image = InputImage.fromFilePath(context, Uri.fromFile(imageFile))
    poseDetector.process(image)
        .addOnSuccessListener { pose ->
            val poseLandmarks = pose.allPoseLandmarks
//            onPoseDetected(poseLandmarks)
            val result = drawPointsOnImage(imageFile, poseLandmarks, context)
            val drawnImageFile = result.first
            val description = result.second
            Log.d("CameraScreen","$description")

            // 콜백으로 그려진 이미지의 Uri를 반환
            onImageProcessed(Uri.fromFile(drawnImageFile), description)
        }
        .addOnFailureListener { e ->
            Log.e("PoseDetection", "Pose detection failed: ${e.message}")
            onImageProcessed(Uri.EMPTY, UnbalanceFigureDto(Pair(0.0f, ""), Pair(0.0f, ""), Pair(0.0f, "")))
        }
}

fun drawPointsOnImage(
    originalImageFile: File,
    poseLandmarks: List<PoseLandmark>,
    context: Context
): Pair<File,UnbalanceFigureDto> {
    // Load original image bitmap
    val bitmap = BitmapFactory.decodeFile(originalImageFile.absolutePath)

    // Read Exif orientation information
    val exif = ExifInterface(originalImageFile.absolutePath)
    val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)

    // Determine rotation angle
    val rotationAngle = when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> 90
        ExifInterface.ORIENTATION_ROTATE_180 -> 180
        ExifInterface.ORIENTATION_ROTATE_270 -> 270
        else -> 0
    }

    // Rotate bitmap if necessary
    val rotatedBitmap = if (rotationAngle != 0) {
        val matrix = Matrix()
        matrix.postRotate(rotationAngle.toFloat())
        Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    } else {
        bitmap
    }

    // Create a mutable bitmap copy from the original bitmap to draw on
    val mutableBitmap = rotatedBitmap.copy(Bitmap.Config.ARGB_8888, true)

    // Create a Canvas to draw on the mutable bitmap
    val canvas = Canvas(mutableBitmap)

    // Paint settings for drawing landmarks
    val paint = Paint().apply {
        color = ContextCompat.getColor(context, android.R.color.background_light)
        style = Paint.Style.FILL
        strokeWidth = 6f
    }

    val mainLandmark : List<PoseLandmark> = listOf(
        poseLandmarks[7],
        poseLandmarks[8],
        poseLandmarks[11],
        poseLandmarks[12],
        poseLandmarks[23],
        poseLandmarks[24],
    )
    Log.d("ReportScreen" , "${getAngle(mainLandmark[0],mainLandmark[1])}")

    val descriptions = UnbalanceFigureDto(
        getDescription(getAngle(mainLandmark[0],mainLandmark[1])),
        getDescription(getAngle(mainLandmark[2],mainLandmark[3])),
        getDescription(getAngle(mainLandmark[4],mainLandmark[5]))
    )

    // Draw circles (landmarks) on the Canvas based on PoseLandmarks
    for (landmark in mainLandmark) {
        Log.d("ReportScreen","(${landmark.landmarkType},${landmark.position})")
        // landmarkType 7,8(귀), 11,12(어깨), 23,24(엉덩이)

        val landmarkPosition = landmark.position
        canvas.drawCircle(
            landmarkPosition.x,
            landmarkPosition.y,
            20f, // Circle radius (adjust as needed)
            paint
        )
    }
    for(i in 0..2){
        canvas.drawLine(
            mainLandmark[i*2].position.x,
            mainLandmark[i*2].position.y,
            mainLandmark[i*2+1].position.x,
            mainLandmark[i*2+1].position.y,
            paint
        )
    }

    // Save the modified bitmap to a new file
    val drawnImageFile = File(context.filesDir, "drawn_${originalImageFile.name}")
    try {
        val outputStream = FileOutputStream(drawnImageFile)
        mutableBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.flush()
        outputStream.close()
    } catch (e: FileNotFoundException) {
        Log.e("drawPointsOnImage", "File not found: ${e.message}")
    } catch (e: IOException) {
        Log.e("drawPointsOnImage", "Error accessing file: ${e.message}")
    }

    return Pair(drawnImageFile, descriptions)
}

suspend fun getScore(imageUri: String): Response<UploadImageResponse> {
    val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor(accessToken!!))
        .build()

    // Retrofit 객체 생성
    val retrofit = Retrofit.Builder()
        .baseUrl("http://35.216.89.227:9090/")
        .client(okHttpClient)
//        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
        .build()

    // Retrofit 서비스 생성
    val service = retrofit.create(ApiService::class.java)

    return service.getResult(imageUri)
}
