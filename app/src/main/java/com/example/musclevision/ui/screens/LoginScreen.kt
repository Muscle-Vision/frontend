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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.getString
import com.example.musclevision.R
import com.example.musclevision.services.ApiService
import com.example.musclevision.ui.theme.MuscleVisionTheme
import com.example.musclevision.ui.theme.dancingScript
import com.example.musclevision.ui.theme.md_theme_dark_background
import com.example.musclevision.ui.theme.md_theme_dark_outline
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.launch
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
    val coroutineScope = rememberCoroutineScope()

    // 구글 로그인 클라이언트 설정
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//        .requestScopes()
        .requestIdToken(getString(context, R.string.default_web_client_id))
        .requestEmail()
        .build()
    Log.d("LoginScreen", getString(context, R.string.default_web_client_id))
    val googleSignInClient: GoogleSignInClient = GoogleSignIn.getClient(context, gso)
    googleSignInClient.signOut()

    // ActivityResultLauncher 설정
    val signInLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        handleSignInResult(task)
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

        Spacer(modifier = Modifier.height(16.dp))

        GoogleSignInButton{signInLauncher.launch(googleSignInClient.signInIntent)}  // 구글 로그인 버튼 추가

        Spacer(modifier = Modifier.height(120.dp))
    }
}
//@Composable
//fun GoogleSignInButton(onGoogleLoginClicked: () -> Unit) {
//    Button(
//        onClick = onGoogleLoginClicked,
//        modifier = Modifier.fillMaxWidth(0.8f)
//    ) {
//        Text("구글 로그인")
//    }
//}

@Composable
fun GoogleSignInButton(onGoogleLoginClicked: () -> Unit) {
    Button(
        onClick = onGoogleLoginClicked,
        modifier = Modifier.fillMaxWidth(0.7f),
        colors = ButtonDefaults.buttonColors(
            containerColor =Color.Transparent,
            contentColor = md_theme_dark_outline
        ),
        shape = RoundedCornerShape(4.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 2.dp,
            pressedElevation = 4.dp
        ),
        border = BorderStroke(1.dp, md_theme_dark_outline) // 흰색 테두리 추가
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_google_logo), // 구글 로고 이미지
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
                    color = md_theme_dark_outline // 텍스트 색상을 흰색으로 설정
                )
            )
        }
    }
}
fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
    try {
        val account = completedTask.getResult(ApiException::class.java)
        Log.d("LoginScreen", "signInResult:success, account: ${account?.email}")
        Log.d("LoginScreen", "signInResult:success, account: ${account?.familyName}")
        Log.d("LoginScreen", "signInResult:success, account: ${account?.givenName}")
        Log.d("LoginScreen", "signInResult:success, account: ${account?.displayName}")
        Log.d("LoginScreen", "signInResult:success, account: ${account?.photoUrl}")
        Log.d("LoginScreen", "signInResult:success, account: ${account?.idToken}")

        val idToken  = account?.idToken
//        Log.d("LoginScreen", idToken!!)
        sendTokenToServer(idToken)
        // 계정 정보를 사용해 상태를 업데이트합니다.
    } catch (e: ApiException) {
        Log.w("LoginScreen", "signInResult:failed code=${e.statusCode}")
    }
}

private fun sendTokenToServer(idToken: String?) {
    val retrofit = Retrofit.Builder()
        .baseUrl("http://220.65.177.57:9090/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService = retrofit.create(ApiService::class.java)

    val call: Call<Void> = apiService.sendTokenToServer(idToken)
    call.enqueue(object : Callback<Void> {
        override fun onResponse(call: Call<Void>, response: Response<Void>) {
            if (response.isSuccessful) {
                Log.d("LoginScreen", "Token sent successfully")
            } else {
                Log.d("LoginScreen", "Token sending failed, ${response.code()}")
            }
        }

        override fun onFailure(call: Call<Void>, t: Throwable) {
            Log.e("LoginScreen", "Error: ${t.message}")
        }
    })
}
@Preview
@Composable
fun LoginPreivew(){
    LoginScreen(onEnrollButtonClicked = {},onLoginButtonClicked = {})
}

