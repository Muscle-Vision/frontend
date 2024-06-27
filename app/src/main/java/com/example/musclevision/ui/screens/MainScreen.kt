package com.example.musclevision.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.example.musclevision.R
import com.example.musclevision.ui.theme.md_theme_dark_onPrimaryContainer

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
            Text(text = "가이드라인", style = MaterialTheme.typography.headlineMedium)
            InfoIconWithTooltip(infoText = stringResource(id = R.string.guideline))
        }
        Spacer(modifier = Modifier.height(20.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(340.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.guideline),
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )
        }

        Text(
            text = "<예시 사진>",
            style = MaterialTheme.typography.headlineSmall,
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
                    modifier = Modifier
                        .padding(start = 24.dp, top = 24.dp)
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
    }
}

@Preview(
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun MainPreivew(){
    MainScreen(onToGalleryButtonClicked = {}, onToCameraButtonClicked = {})
}