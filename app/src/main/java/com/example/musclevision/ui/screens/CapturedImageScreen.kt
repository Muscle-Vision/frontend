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
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
        Text(
            text = "분석할 사진",
            modifier = Modifier.padding(vertical = 8.dp),
            style = MaterialTheme.typography.headlineMedium)
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
                                if (response.isSuccessful) {
                                     val uri = response.body()?.photoRoute
                                    onAnalyzeButtonClicked(uri!!)
                                }
                            }
                        } catch (e: Exception) {
                            Log.e("CameraScreen", "Error uploading image: ${e.message}")
                        }
                    }
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
