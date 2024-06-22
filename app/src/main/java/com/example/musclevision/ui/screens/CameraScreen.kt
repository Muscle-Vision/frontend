@file:Suppress("NAME_SHADOWING")

package com.example.musclevision.ui.screens

import android.content.Context
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import com.example.musclevision.MainActivity
import com.example.musclevision.data.UploadImageResponse
import com.example.musclevision.services.ApiService

import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.gson.GsonBuilder
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.Executor
import kotlin.reflect.typeOf


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen(
    onNextButtonClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val cameraProvider = remember { ProcessCameraProvider.getInstance(context) }
    val imageCapture = remember { ImageCapture.Builder()
        .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
        .build() }
    val camera = remember { mutableStateOf<Camera?>(null) }
    val previewView = remember { PreviewView(context) }

    val permissionState = rememberPermissionState(android.Manifest.permission.CAMERA)

    val outputDirectory = remember { File(context.filesDir, "camera_capture").apply { mkdirs() } }

    val lifecycleOwner = LocalLifecycleOwner.current

    // 카메라 변수를 초기화하기 위한 LaunchedEffect
    LaunchedEffect(permissionState) {
        if (permissionState.status.isGranted) {
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            val preview = Preview.Builder().build()
            preview.setSurfaceProvider(previewView.surfaceProvider)
            val cameraProviderInstance = cameraProvider.get()
            camera.value = cameraProviderInstance.bindToLifecycle(
                (context as ComponentActivity),
                cameraSelector,
                preview,
                imageCapture
            )
        }
    }
    // 권한이 승인되지 않은 경우 다이얼로그 표시
    if (!permissionState.status.isGranted) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text("Camera Permission Required") },
            text = { Text("To use the camera, you need to grant camera permission.") },
            confirmButton = {
                Button(
                    onClick = {
                        permissionState.launchPermissionRequest()
                    }
                ) {
                    Text("Grant Permission")
                }
            }
        )
    }
    Column(modifier = modifier.fillMaxSize()) {
        // Camera Preview
        camera.value?.let { camera ->
            AndroidView(
                { previewView },
                modifier = Modifier.weight(1f)
            )
        }

        // Capture Button
        Button(
//            onClick = onNextButtonClicked,
            onClick = {
                captureImage(
                    imageCapture = imageCapture,
                    outputDirectory = outputDirectory,
                    onSuccess = { file ->
                        lifecycleOwner.lifecycleScope.launch {
                            try {
                                val response = uploadImage(file)
                                Log.d("CameraScreen","응답으로 받은것: $response , $file")
                                if (response.isSuccessful) {
                                    // 업로드 성공
                                    val responseBody = response.body()
                                    // 서버 응답에 따라 적절한 처리를 수행합니다.
                                    Log.d("CameraScreen","성공 : $responseBody")
                                } else {
                                    // 업로드 실패
                                    val errorMessage = response.message()

                                    // 오류 처리
                                    Log.d("CameraScreen","에러로 받은것: $errorMessage")
                                }
                            } catch (e: Exception) {
                                // 예외 처리
                                Log.e("CameraScreen", "Error uploading image: ${e.message}")
                            }
                        }
                        // 이미지 캡처 성공 시 여기에 원하는 작업을 추가하세요.
                        Log.d("CameraScreen", "Image captured and saved: ${file.absolutePath}")
                    },
                    onError = { errorMessage ->
                        // 이미지 캡처 실패 시 여기에 오류 처리를 추가하세요.
                        Log.e("CameraScreen", "Error capturing image: $errorMessage")
                    },
                    context
                )
            },
            modifier = Modifier
                .size(100.dp)
                .align(Alignment.CenterHorizontally) // Center horizontally
        ) {
            Text("Capture")
        }
    }
}

fun captureImage(
    imageCapture: ImageCapture,
    outputDirectory: File,
    onSuccess: (File) -> Unit,
    onError: (String) -> Unit,
    context: Context
) {
    // 이미지 파일 생성
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val imageFileName = "IMG_${timeStamp}.jpg"
    val outputFile = File(outputDirectory, imageFileName)

    // 이미지 캡처 및 저장
    val outputOptions = ImageCapture.OutputFileOptions.Builder(outputFile).build()
    imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                onSuccess(outputFile)
                // 이미지 캡처 및 저장 후에 이미지를 서버로 업로드
            }
            override fun onError(exception: ImageCaptureException) {
                onError(exception.message ?: "Unknown error occurred.")
            }
        }
    )
}

suspend fun uploadImage(imageFile: File): Response<UploadImageResponse> {
    // Retrofit 객체 생성
    val retrofit = Retrofit.Builder()
        .baseUrl("http://35.216.89.227:9090/")
//        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
        .build()

    // Retrofit 서비스 생성
    val service = retrofit.create(ApiService::class.java)

    // 이미지 파일을 RequestBody로 변환
    val requestFile = RequestBody.create(MediaType.parse("image/jpeg"), imageFile)
    val body = MultipartBody.Part.createFormData("image", imageFile.name, requestFile)
    Log.d("CameraScreen","$requestFile, ${body.javaClass}")
    // 서버로 업로드 요청
    return service.uploadImage(body)
}

