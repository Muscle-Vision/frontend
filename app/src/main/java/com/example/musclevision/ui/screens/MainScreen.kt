package com.example.musclevision.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MainScreen(
    onToGalleryButtonClicked: ()-> Unit,
    onToCameraButtonClicked: ()-> Unit,
    modifier: Modifier = Modifier
){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Button(onClick = onToCameraButtonClicked) {
            Text("카메라 찍기")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = onToGalleryButtonClicked) {
            Text("사진 선택")
        }
    }

}