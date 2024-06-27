@file:Suppress("NAME_SHADOWING")

package com.example.musclevision.ui.screens

import android.content.Context
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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    DisposableEffect(Unit) {
        onDispose {
            camera.value?.cameraControl?.cancelFocusAndMetering()
            cameraProvider.get().unbindAll()
        }
    }
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
        camera.value?.let { camera ->
            AndroidView(
                { previewView },
                modifier = Modifier.weight(1f)
            )
        }

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
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            modifier = Modifier
                .size(100.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Icon(
                imageVector = Icons.Filled.PhotoCamera,
                modifier = Modifier.size(80.dp),
                contentDescription ="촬영버튼" )
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
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val imageFileName = "IMG_${timeStamp}.jpg"
    val outputFile = File(outputDirectory, imageFileName)

    val outputOptions = ImageCapture.OutputFileOptions.Builder(outputFile).build()
    imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                onSuccess(outputFile)
            }
            override fun onError(exception: ImageCaptureException) {
                onError(exception.message ?: "Unknown error occurred.")
            }
        }
    )
}
