@file:Suppress("NAME_SHADOWING")

package com.example.musclevision.ui.screens

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.Uri
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen(
    onImageCaptured: (Uri) -> Unit,
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
    // Composable이 폐기될 때 카메라 리소스 해제
    DisposableEffect(Unit) {
        onDispose {
            camera.value?.cameraControl?.cancelFocusAndMetering()
            cameraProvider.get().unbindAll()
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
            onClick = {
                captureImage(
                    imageCapture = imageCapture,
                    outputDirectory = outputDirectory,
                    onSuccess = { file ->
                        onImageCaptured(Uri.fromFile(file))
                    },
                    onError = { errorMessage ->
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

@Composable
fun LevelCanvas(roll: Float) {
    Canvas(modifier = Modifier.size(200.dp)) {
        drawLevel(this, roll)
    }
}

fun drawLevel(drawScope: DrawScope, roll: Float) {
    val centerX = drawScope.size.width / 2
    val centerY = drawScope.size.height / 2

    // 빨간색 수평선
    drawScope.drawLine(
        color = Color.Red,
        start = androidx.compose.ui.geometry.Offset(centerX - 100, centerY),
        end = androidx.compose.ui.geometry.Offset(centerX + 100, centerY),
        strokeWidth = 5f
    )

    // 파란색 원 (수평기 인디케이터)
    val rollInPixels = roll * 5 // Roll 각도에 따라 픽셀로 변환하여 조정
    val indicatorY = centerY + rollInPixels
    drawScope.drawCircle(
        color = Color.Blue,
        center = androidx.compose.ui.geometry.Offset(centerX, indicatorY),
        radius = 10f
    )


}
