package com.example.musclevision.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.musclevision.R
import com.example.musclevision.ui.theme.MuscleVisionTheme
import com.example.musclevision.ui.theme.dancingScript


@Composable
fun LoginScreen(
    onEnrollButtonClicked: ()-> Unit,
    onLoginButtonClicked: () -> Unit,
    modifier: Modifier = Modifier
){
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Welcome",
            style = TextStyle(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFFC7BFFF),Color.White)
                )
            ),
            fontFamily = dancingScript,
            fontSize = 60.sp
        )
        Text(
            text = "MuscleVision",
            style = TextStyle(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFFC7BFFF),Color.White)
                ),
            ),
            fontFamily = dancingScript,
            fontSize = 60.sp
        )
        Spacer(modifier = Modifier.height(60.dp))
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("사용자 이름") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(0.8f),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("비밀번호") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(0.8f),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            )
        )
        Spacer(modifier = Modifier.height(30.dp))

        BasicText(
            text = "회원가입하기",
            style = TextStyle(
                textDecoration = TextDecoration.Underline,
                color = MaterialTheme.colorScheme.onPrimaryContainer
                ),
            modifier = Modifier.clickable{onEnrollButtonClicked()}
        )

        Spacer(modifier = Modifier.height(30.dp))

        Button(onClick = onLoginButtonClicked) {
            Text("로그인")
        }

        Spacer(modifier = Modifier.height(120.dp))
    }
}


@Preview
@Composable
fun LoginPreivew(){
    LoginScreen(onEnrollButtonClicked = {},onLoginButtonClicked = {})
}

