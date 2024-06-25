package com.example.musclevision.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.musclevision.data.MemberRequestDto
import com.example.musclevision.data.MemberResponseDto
import com.example.musclevision.services.RetrofitClient
import com.example.musclevision.ui.theme.md_theme_dark_onPrimaryContainer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun EnrollScreen(
    onFinishEnrollButtonClicked: ()-> Unit,
    modifier: Modifier = Modifier
){
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisibility by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "회원가입", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(30.dp))
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("이름") }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.width(OutlinedTextFieldDefaults.MinWidth)
        ) {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("이메일") },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = { requestEmailVerification(email) },
                contentPadding = PaddingValues(5.dp),
                colors = ButtonDefaults.buttonColors(containerColor = md_theme_dark_onPrimaryContainer)
            ) {
                Text("인증요청")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("비밀번호") },
            visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
                    Icon(
                        imageVector = if (passwordVisibility) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = if (passwordVisibility) "비밀번호 숨기기" else "비밀번호 보기"
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(30.dp))

        Button(onClick = {
            onFinishEnrollButtonClicked()
            signup(email, password)
            },
            modifier = Modifier.fillMaxWidth(0.7f),
            colors = ButtonDefaults.buttonColors(containerColor = md_theme_dark_onPrimaryContainer)
        ) {
            Text("회원가입")
        }
    }
}

fun requestEmailVerification(email: String) {
    Log.d("emailAuth", email)
    RetrofitClient.instance.emailAuth(email).enqueue(object : Callback<String> {
        override fun onResponse(call: Call<String>, response: Response<String>) {
            if (response.isSuccessful) {
                val authResponse = response.body()
                Log.d("emailAuth", "emailAuth successful: ${authResponse}")
            } else {
                Log.d("emailAuth", "emailAuth failed: ${response.errorBody()?.string()}")
            }
        }

        override fun onFailure(call: Call<String>, t: Throwable) {
            Log.e("emailAuth", "emailAuth error", t)
        }
    })
}

private fun signup(loginId: String, password: String) {
    val request = MemberRequestDto(loginId, password)
    RetrofitClient.instance.signup(request).enqueue(object : Callback<MemberResponseDto> {
        override fun onResponse(call: Call<MemberResponseDto>, response: Response<MemberResponseDto>) {
            if (response.isSuccessful) {
                val memberResponse = response.body()
                Log.d("Signup", "Signup successful: ${memberResponse?.loginId}")
            } else {
                Log.d("Signup", "Signup failed: ${response.errorBody()?.string()}")
            }
        }

        override fun onFailure(call: Call<MemberResponseDto>, t: Throwable) {
            Log.e("Signup", "Signup error", t)
        }
    })
}
