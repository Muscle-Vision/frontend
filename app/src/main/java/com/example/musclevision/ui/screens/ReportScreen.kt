package com.example.musclevision.ui.screens

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.musclevision.R


@Composable
fun ReportScreen(
    onNextButtonClicked: ()-> Unit,
    modifier: Modifier = Modifier
){
    Button(
        onClick = onNextButtonClicked,
        modifier = modifier
    ) {
        Text(stringResource(R.string.analyze))
    }
}