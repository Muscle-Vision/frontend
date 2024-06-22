package com.example.musclevision.ui.screens

import android.net.Uri
import android.widget.Button
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter

@Composable
fun CapturedImageScreen(
    onRetakeButtonClicked : () -> Unit,
    onAnalyzeButtonClicked : () -> Unit,
    imageUri: Uri,
    modifier: Modifier = Modifier
) {
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
            androidx.compose.material3.Button(onClick = onRetakeButtonClicked) {
                Text("다시찍기")
            }
            androidx.compose.material3.Button(onClick = onAnalyzeButtonClicked) {
                Text("분석하기")
            }
        }
    }
}