package com.example.musclevision.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import coil.compose.rememberImagePainter
import com.example.musclevision.services.AuthManager
import com.example.musclevision.services.uploadImage
import com.example.musclevision.services.uriToFile
import com.example.musclevision.ui.theme.md_theme_dark_onPrimaryContainer
import kotlinx.coroutines.launch


@Composable
fun GalleryScreen(onSelectButtonClicked: (Uri, String) -> Unit, modifier: Modifier = Modifier) {
    Log.d("GalleryScreen","${AuthManager.accessToken}")
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var analysedImageUri by remember { mutableStateOf<Uri?>(null) }
    var permissionGranted by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var receivedUri:String? = null

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
        }
    }


    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        permissionGranted = isGranted
        if (isGranted) {
            galleryLauncher.launch("image/*")
        }
    }

    LaunchedEffect(Unit) {
        when {
            ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED -> {
                permissionGranted = true
                galleryLauncher.launch("image/*")
            }
            else -> {
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                galleryLauncher.launch("image/*")
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        selectedImageUri?.let { uri ->
            val painter: Painter = rememberImagePainter(
                data = uri,
                builder = {
                    crossfade(true)
                }
            )
            Text(text = "이 사진으로 하시겠습니까?", style = MaterialTheme.typography.headlineMedium)
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

            Spacer(modifier = Modifier.height(24.dp))

            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly){
                Button(
                    onClick = { galleryLauncher.launch("image/*") },
                    colors = ButtonDefaults.buttonColors(containerColor = md_theme_dark_onPrimaryContainer),
                ) {
                    Text("다시 고르기")
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = "앨범")
                }

                Button(
                    onClick = {
                        val file = uriToFile(uri,context)
                        if(file != null) {
                            lifecycleOwner.lifecycleScope.launch {
                                try {
                                    val response = uploadImage(file)
                                    Log.d("GalleryScreen", "응답으로 받은것 + 내가준 파일: ${response.body()} , $file")
                                    if (response.isSuccessful) {
                                        // 업로드 성공
                                        receivedUri = response.body()?.photoRoute
                                        // 서버 응답에 따라 적절한 처리를 수행합니다.
                                        Log.d("GalleryScreen", "성공 : ${response.body()?.photoRoute}, $receivedUri")
                                        onSelectButtonClicked(uri, receivedUri!!)
                                    } else {
                                        // 업로드 실패
                                        val errorMessage = response.message()
                                        // 오류 처리
                                        Log.d("GalleryScreen", "에러로 받은것: $errorMessage")
                                    }
                                } catch (e: Exception) {
                                    // 예외 처리
                                    Log.e(" GalleryScreen", "Error sending image: ${e.message}")
                                }
                            }
                            Log.d("GalleryScreen", "Image captured and saved: ${file.absolutePath}")
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = md_theme_dark_onPrimaryContainer)
                ) {
                    Text("분석하기")
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(imageVector = Icons.Default.ReceiptLong, contentDescription = "카메라")
                }
                Log.d("selected",uri.toString())
            }
        }
    }
}
