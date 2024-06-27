package com.example.musclevision.ui.screens

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.getString
import com.example.musclevision.R
import com.example.musclevision.data.LoginRequestDto
import com.example.musclevision.data.TokenDto
import com.example.musclevision.services.ApiService
import com.example.musclevision.services.AuthManager
import com.example.musclevision.services.RetrofitClient
import com.example.musclevision.ui.theme.dancingScript
import com.example.musclevision.ui.theme.md_theme_dark_onPrimaryContainer
import com.example.musclevision.ui.theme.md_theme_dark_outline
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


@Composable
fun LoginScreen(
    onEnrollButtonClicked: ()-> Unit,
    onLoginButtonClicked: () -> Unit,
    modifier: Modifier = Modifier
){
    val context = LocalContext.current
    var passwordVisibility by remember { mutableStateOf(false) }

    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(getString(context, R.string.default_web_client_id))
        .requestEmail()
        .build()
    val googleSignInClient: GoogleSignInClient = GoogleSignIn.getClient(context, gso)
    googleSignInClient.signOut()

    val signInLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        handleSignInResult(task,onLoginButtonClicked)
    }

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
            ),
            visualTransformation =
                if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
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

        BasicText(
            text = "회원가입",
            style = TextStyle(
                textDecoration = TextDecoration.Underline,
                color = md_theme_dark_onPrimaryContainer
                ),
            modifier = Modifier.clickable{onEnrollButtonClicked()}
        )

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = {
                onLoginButtonClicked()
                login(username, password)
            },
            modifier = Modifier.fillMaxWidth(0.7f),
            colors = ButtonDefaults.buttonColors(containerColor = md_theme_dark_onPrimaryContainer)
        ) {
            Text("로그인")
        }

        Spacer(modifier = Modifier.height(16.dp))

        GoogleSignInButton{signInLauncher.launch(googleSignInClient.signInIntent)}

        Spacer(modifier = Modifier.height(120.dp))
    }
}

@Composable
fun GoogleSignInButton(onGoogleLoginClicked: () -> Unit) {
    Button(
        onClick = onGoogleLoginClicked,
        modifier = Modifier.fillMaxWidth(0.7f),
        colors = ButtonDefaults.buttonColors(
            containerColor =Color.Transparent,
            contentColor = md_theme_dark_outline
        ),
        shape = ButtonDefaults.shape,
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 2.dp,
            pressedElevation = 4.dp
        ),
        border = BorderStroke(1.dp, md_theme_dark_outline)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_google_logo),
                contentDescription = "Google Logo",
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "구글 계정으로 로그인",
                style = TextStyle(
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = md_theme_dark_outline
                )
            )
        }
    }
}
fun handleSignInResult(completedTask: Task<GoogleSignInAccount>, onLoginButtonClicked: () -> Unit) {
    try {
        val account = completedTask.getResult(ApiException::class.java)

        val idToken  = account?.idToken
        sendTokenToServer(idToken, onLoginButtonClicked)
    } catch (e: ApiException) {
        Log.w("LoginScreen", "signInResult:failed code=${e.statusCode} message=${e.message}\"")
    }
}

private fun sendTokenToServer(idToken: String?, onLoginButtonClicked: () -> Unit) {
    val retrofit = Retrofit.Builder()
        .baseUrl("http://35.216.89.227:9090/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService = retrofit.create(ApiService::class.java)

    val call: Call<TokenDto> = apiService.sendTokenToServer(idToken)
    call.enqueue(object : Callback<TokenDto> {
        override fun onResponse(call: Call<TokenDto>, response: Response<TokenDto>) {
            if (response.isSuccessful) {
                val tokenResponse = response.body()
                AuthManager.accessToken = tokenResponse?.accessToken
                AuthManager.refreshToken = tokenResponse?.refreshToken
                onLoginButtonClicked()
            }
        }

        override fun onFailure(call: Call<TokenDto>, t: Throwable) {
            Log.e("LoginScreen", "Error: ${t.message}")
        }
    })
}

private fun login(loginId: String, password: String) {
    val request = LoginRequestDto(loginId, password)
    RetrofitClient.instance.login(request).enqueue(object : Callback<TokenDto> {
        override fun onResponse(call: Call<TokenDto>, response: Response<TokenDto>) {
            if (response.isSuccessful) {
                val tokenResponse = response.body()
                AuthManager.accessToken = tokenResponse?.accessToken
                AuthManager.refreshToken = tokenResponse?.refreshToken
            }
        }

        override fun onFailure(call: Call<TokenDto>, t: Throwable) {

        }
    })
}

private fun retryLoginWithRefreshToken(loginId: String, password: String) {
    AuthManager.refreshAccessToken { success ->
        if (success) {
            loginWithNewAccessToken(loginId, password)
        }
    }
}

private fun loginWithNewAccessToken(loginId: String, password: String) {

    val accessToken = AuthManager.accessToken

    login(loginId, password)
}

@Preview
@Composable
fun LoginPreivew(){
    LoginScreen(onEnrollButtonClicked = {},onLoginButtonClicked = {})
}

