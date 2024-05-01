@file:Suppress("NAME_SHADOWING")

package com.example.musclevision.ui.screens

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen(
    onNextButtonClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val cameraProvider = remember { ProcessCameraProvider.getInstance(context) }
    val imageCapture = remember { ImageCapture.Builder().build() }
    val camera = remember { mutableStateOf<Camera?>(null) }
    val previewView = remember { PreviewView(context) }

    val permissionState = rememberPermissionState(android.Manifest.permission.CAMERA)

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
    camera.value?.let { camera ->
        AndroidView(
            { previewView },
            modifier = Modifier.fillMaxSize()
        )
    }
    Button(
        onClick = onNextButtonClicked,
        modifier = Modifier.size(50.dp)
    ) {
        Text("Next")
    }
}

@Composable
fun CameraPreview(camera: Camera) {
    val context = LocalContext.current
    val previewView = remember { PreviewView(context) }

    AndroidView(
        { previewView },
        modifier = Modifier.fillMaxSize()
    ) { view ->
        val preview = Preview.Builder().build()
        preview.setSurfaceProvider(view.surfaceProvider)
    }
}

@androidx.compose.ui.tooling.preview.Preview
@Composable
fun PreviewCameraScreen(){
    CameraScreen(onNextButtonClicked = { /*TODO*/ })
}
