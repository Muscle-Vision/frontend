package com.example.musclevision.ui.screens

import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import coil.compose.ImagePainter
import com.example.musclevision.R
import com.example.musclevision.ui.theme.md_theme_dark_onPrimaryContainer
import com.example.musclevision.ui.theme.md_theme_dark_outline

@Composable
fun MainScreen(
    onToGalleryButtonClicked: ()-> Unit,
    onToCameraButtonClicked: ()-> Unit,
    modifier: Modifier = Modifier
){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(text = "가이드라인", style = MaterialTheme.typography.headlineLarge)
            InfoIconWithTooltip(infoText = "1.카메라로 사진을 찍거나 앨범에서 사진을 고릅니다." +
                    "\n2.되도록 몸에 힘을 빼고 정면에서 찍은 전신사진을 사용하세요." +
                    "\n3.목, 어깨, 골반의 불균형이 탐지됩니다.")
        }
        Spacer(modifier = Modifier.height(20.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(340.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.muscle_vision_logo_center),
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )
        }

        Text(
            text = "<예시 사진>",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(0.dp)
        )

        Spacer(modifier = Modifier.height(60.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = onToCameraButtonClicked,
                colors = ButtonDefaults.buttonColors(containerColor = md_theme_dark_onPrimaryContainer)
            ) {
                Text("카메라 찍기")
                Spacer(modifier = Modifier.width(6.dp))
                Icon(imageVector = Icons.Default.CameraAlt, contentDescription = "카메라")
            }
            Button(
                onClick = onToGalleryButtonClicked,
                colors = ButtonDefaults.buttonColors(containerColor = md_theme_dark_onPrimaryContainer)

            ) {
                Text("내앨범")
                Spacer(modifier = Modifier.width(6.dp))
                Icon(imageVector = Icons.Default.AddPhotoAlternate, contentDescription = "앨범")
            }

        }
    }
}

@Composable
fun InfoIconWithTooltip(
    infoText: String,
    modifier: Modifier = Modifier
) {
    var showTooltip by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .clickable { showTooltip = !showTooltip }
            .padding(8.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.Info,
            contentDescription = "Info",
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )

        if (showTooltip) {
            Popup(
                alignment = Alignment.TopEnd,
                properties = PopupProperties(focusable = true),
                onDismissRequest = { showTooltip = false },
            ) {
                Box(
                    modifier = Modifier.padding(start = 24.dp, top = 24.dp)
                        .background(Color.Black, shape = RoundedCornerShape(16.dp))
                        .padding(8.dp)
                ) {
                    Text(
                        text = infoText,
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }
            }
        }
        Log.d("tooltip","${showTooltip}")
    }
}

@Preview(
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun MainPreivew(){
    MainScreen(onToGalleryButtonClicked = {}, onToCameraButtonClicked = {})
}