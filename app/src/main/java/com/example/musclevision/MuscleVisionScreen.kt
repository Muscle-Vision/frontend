package com.example.musclevision

import android.net.Uri
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.musclevision.ui.screens.CameraScreen
import com.example.musclevision.ui.screens.CapturedImageScreen
import com.example.musclevision.ui.screens.EnrollScreen
import com.example.musclevision.ui.screens.GalleryScreen
import com.example.musclevision.ui.screens.LoginScreen
import com.example.musclevision.ui.screens.MainScreen
import com.example.musclevision.ui.screens.ReportScreen

enum class MuscleVisionScreen(@StringRes val title: Int) {
    Greeting(title = R.string.app_name),
    Login(title = R.string.login),
    Enroll(title = R.string.enroll),
    Main(title = R.string.main),
    Camera(title = R.string.camera),
    Gallery(title = R.string.gallery),
    CapturedImage(title = R.string.captured_image),
    Report(title = R.string.analyze)
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MuscleVisionAppBar(modifier: Modifier = Modifier,
                       currentScreen: MuscleVisionScreen,
                       canNavigateBack: Boolean,
                       navigateUp: () -> Unit) {
    CenterAlignedTopAppBar(
        title = {
            Image(
                painter = painterResource(id = R.drawable.muscle_vision_logo_transparent),
                contentDescription = "app logo",
                modifier = Modifier.size(80.dp)
            )
        },
        modifier = modifier,
        navigationIcon ={
            if(canNavigateBack){
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "back"
                    )
                }
            }
        }
    )
}


@Preview
@Composable
fun MuscleVisionApp(
    navController: NavHostController = rememberNavController()
){
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = MuscleVisionScreen.valueOf(
        backStackEntry?.destination?.route?.substringBefore("?") ?: MuscleVisionScreen.Login.name
    )
    Scaffold(
        topBar = {
            MuscleVisionAppBar(
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() }
            )
        }
    ){  innerPadding ->
        NavHost(
            navController = navController,
            startDestination = MuscleVisionScreen.Login.name,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
        ){
            composable(route = MuscleVisionScreen.Login.name) {
                LoginScreen(
                    onEnrollButtonClicked = {
                        navController.navigate(MuscleVisionScreen.Enroll.name)
                    },
                    onLoginButtonClicked = {
                        navController.navigate(MuscleVisionScreen.Main.name)
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                )
            }
            composable(route = MuscleVisionScreen.Enroll.name) {
                EnrollScreen(
                    onFinishEnrollButtonClicked = {
                        navController.navigate(MuscleVisionScreen.Login.name)
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                )
            }
            composable(route = MuscleVisionScreen.Main.name) {
                MainScreen(
                    onToGalleryButtonClicked = {
                        navController.navigate(MuscleVisionScreen.Gallery.name)
                    },
                    onToCameraButtonClicked = {
                        navController.navigate(MuscleVisionScreen.Camera.name)
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                )
            }
            composable(route = MuscleVisionScreen.Gallery.name) {
                GalleryScreen(
                    onSelectButtonClicked = { uri, receivedUri ->
                        navController.navigate("${MuscleVisionScreen.Report.name}?uri=${Uri.encode(uri.toString())}&receivedUri=${Uri.encode(receivedUri)}")
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                )
            }
            composable(route = MuscleVisionScreen.Camera.name) {
                    CameraScreen(
                        onImageCaptured = { uri ->
                            navController.navigate("${MuscleVisionScreen.CapturedImage.name}?uri=${Uri.encode(uri.toString())}")
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    )
            }
            composable(
                route = "${MuscleVisionScreen.CapturedImage.name}?uri={uri}",
                arguments = listOf(
                    navArgument("uri"){type = NavType.StringType}
                )
            ) { backStackEntry ->
                val uriString = backStackEntry.arguments?.getString("uri")
                uriString?.let {uri ->
                    CapturedImageScreen(
                        onAnalyzeButtonClicked = { receivedUri ->
                            navController.navigate("${MuscleVisionScreen.Report.name}?uri=${Uri.encode(uri.toString())}&receivedUri=${Uri.encode(receivedUri)}")
                        },
                        onRetakeButtonClicked = {
                            navController.navigate(MuscleVisionScreen.Camera.name)
                        },
                        imageUri = Uri.parse(uri)
                    )
                }
            }
            composable(
                route = "${MuscleVisionScreen.Report.name}?uri={uri}&receivedUri={receivedUri}",
                arguments = listOf(
                    navArgument("uri") { type = NavType.StringType },
                    navArgument("receivedUri"){type = NavType.StringType}
                )) {backStackEntry ->
                    val uriString = backStackEntry.arguments?.getString("uri")
                    val receivedUriString = backStackEntry.arguments?.getString("receivedUri")
                    uriString?.let{
                        uri ->
                        receivedUriString?.let{
                            receivedUri ->
                            ReportScreen(
                                onNextButtonClicked = {
                                    navController.navigate(MuscleVisionScreen.Greeting.name)
                                },
                                imageUri = Uri.parse(uri),
                                receivedUri = receivedUri,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp)
                            )
                    }

                    }
            }
        }
    }
}

