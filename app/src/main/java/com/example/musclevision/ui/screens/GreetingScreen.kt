package com.example.musclevision.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.musclevision.MuscleVisionAppBar
import com.example.musclevision.MuscleVisionScreen
import com.example.musclevision.R

@Composable
fun GreetingScreen(
    onNextButtonClicked: ()-> Unit,
    modifier: Modifier = Modifier
){
    Button(
        onClick = onNextButtonClicked,
        modifier = modifier
    ) {
        Text(stringResource(R.string.greeting))
    }
}
@Preview
@Composable
fun GreetingPreview(
){
    GreetingScreen(onNextButtonClicked = {})
}
