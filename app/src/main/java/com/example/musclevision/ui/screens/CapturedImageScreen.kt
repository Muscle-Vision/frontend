package com.example.musclevision.ui.screens

import android.net.Uri
import android.util.Log
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
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import coil.compose.rememberImagePainter
import com.example.musclevision.services.AuthManager
import com.example.musclevision.services.uploadImage
import com.example.musclevision.services.uriToFile
import com.example.musclevision.ui.theme.md_theme_dark_onPrimaryContainer
import kotlinx.coroutines.launch

@Composable
fun CapturedImageScreen(
    onRetakeButtonClicked : () -> Unit,
    onAnalyzeButtonClicked : (String) -> Unit,
    imageUri: Uri,
    modifier: Modifier = Modifier
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("분석할 사진", modifier = Modifier.padding(vertical = 8.dp))
        val painter: Painter = rememberImagePainter(data = imageUri)
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = onRetakeButtonClicked,
                colors = ButtonDefaults.buttonColors(containerColor = md_theme_dark_onPrimaryContainer)
            ) {
                Text("다시찍기")
                Spacer(modifier = Modifier.width(6.dp))
                Icon(imageVector = Icons.Default.Refresh, contentDescription = "카메라")
            }
            Button(
                onClick = {
                    val file = uriToFile(imageUri, context)
                    lifecycleOwner.lifecycleScope.launch {
                        try {
                            if(file!=null){
                                val response = uploadImage(file)
                                Log.d("CameraScreen","응답으로 받은것: $response , $file")
                                if (response.isSuccessful) {
                                    // 업로드 성공
                                     val uri = response.body()?.photoRoute
                                    // 서버 응답에 따라 적절한 처리를 수행합니다.
                                    Log.d("CameraScreen","성공 : $response.body()?.photoRoute")
                                    onAnalyzeButtonClicked(uri!!)
                                } else {
                                    // 업로드 실패
                                    val errorMessage = response.message()

                                    // 오류 처리
                                    Log.d("CameraScreen","에러로 받은것: $errorMessage")
                                }
                            }
                        } catch (e: Exception) {
                            // 예외 처리
                            Log.e("CameraScreen", "Error uploading image: ${e.message}")
                        }
                    }
                    Log.d("CameraScreen", "Image captured and saved: ${file?.absolutePath}")
            },
            colors = ButtonDefaults.buttonColors(containerColor = md_theme_dark_onPrimaryContainer)
            ) {
                Text("분석하기")
                Spacer(modifier = Modifier.width(6.dp))
                Icon(imageVector = Icons.Default.ReceiptLong, contentDescription = "카메라")
            }
        }
    }
}
