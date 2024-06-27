package com.example.musclevision.ui.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.media.ExifInterface
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SentimentDissatisfied
import androidx.compose.material.icons.filled.SentimentSatisfiedAlt
import androidx.compose.material.icons.filled.SentimentVeryDissatisfied
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil.compose.rememberImagePainter
import com.example.musclevision.R
import com.example.musclevision.data.UnbalanceFigureDto
import com.example.musclevision.data.UploadImageResponse
import com.example.musclevision.services.ApiService
import com.example.musclevision.services.AuthInterceptor
import com.example.musclevision.services.AuthManager.accessToken
import com.example.musclevision.services.getAngle
import com.example.musclevision.services.getDescription
import com.example.musclevision.services.uriToFile
import com.example.musclevision.ui.FirstNeckStretching
import com.example.musclevision.ui.FirstPelvisStretching
import com.example.musclevision.ui.FirstShoulderStretching
import com.example.musclevision.ui.SecondNeckStretching
import com.example.musclevision.ui.SecondPelvisStretching
import com.example.musclevision.ui.SecondShoulderStretching
import com.example.musclevision.ui.ThirdPelvisStretching
import com.example.musclevision.ui.ThirdShoulderStretching
import com.example.musclevision.ui.theme.md_theme_dark_onPrimaryContainer
import com.google.gson.GsonBuilder
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.PoseLandmark
import com.google.mlkit.vision.pose.accurate.AccuratePoseDetectorOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import kotlin.math.absoluteValue
import kotlin.random.Random

private const val BASE_URL = "http://35.216.89.227:9090/"
@Composable
fun ReportScreen(
    onNextButtonClicked: ()-> Unit,
    imageUri : Uri,
    modifier: Modifier = Modifier,
    receivedUri: String
){

    var analyzedImageUri by remember { mutableStateOf<Uri?>(null) }
    var analyzedFigures by remember { mutableStateOf<UnbalanceFigureDto?>(null) }

    val scope = rememberCoroutineScope()
    var isBottomSheetOpen by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val file by remember(imageUri) {
        mutableStateOf(uriToFile(imageUri, context))
    }

    var score by remember { mutableStateOf<String?>("") }

    val updatedAnalyzedImageUri by rememberUpdatedState(newValue = analyzedImageUri)

    LaunchedEffect(file) {
        if (file != null && updatedAnalyzedImageUri == null) {
            detectPose(file!!, context) { uri, description ->
                analyzedImageUri = uri
                analyzedFigures = description
            }
        }
        try {
            val response = withContext(Dispatchers.IO) { getScore(receivedUri) }
            if (response.isSuccessful) {
                score = response.body()?.result
            } else {
                val errorMessage = response.message()
            }
        } catch (e: Exception) {
            Log.e("ReportScreenGetScore", "Error get score: ${e.message}")
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "나의 점수 :$score",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        if (analyzedImageUri != null) {
            val painter = rememberImagePainter(
                data = analyzedImageUri,
                builder = {
                    crossfade(true)
                    error(R.drawable.ic_google_logo)
                }
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(340.dp)
            ) {
                Image(
                    painter = painter,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(40.dp))
                )
            }
            Surface(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                color = Color(0xFF48464F),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        modifier = Modifier
                            .weight(0.2f)
                            .height(130.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(" ")
                        Text("목")
                        Text("어깨")
                        Text("골반")
                    }

                    Column(
                        modifier = Modifier
                            .weight(0.2f)
                            .height(130.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween

                    ) {
                        Text("각도")
                        Text(String.format("%.2f", analyzedFigures?.neckFigure?.first))
                        Text(String.format("%.2f", analyzedFigures?.shoulderFigure?.first))
                        Text(String.format("%.2f", analyzedFigures?.pelvisFigure?.first))
                    }
                    Column(
                        modifier = Modifier
                            .weight(0.4f)
                            .height(130.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween

                    ) {
                        Text("진단")
                        Text("${analyzedFigures?.neckFigure?.second}")
                        Text("${analyzedFigures?.shoulderFigure?.second}")
                        Text("${analyzedFigures?.pelvisFigure?.second}")
                    }
                    Column(
                        modifier = Modifier
                            .weight(0.2f)
                            .height(130.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween

                    ) {
                        Text("")
                        Icon(
                            imageVector =
                            when (analyzedFigures?.neckFigure?.second) {
                                "정상" -> Icons.Default.SentimentSatisfiedAlt
                                "약간의 불균형" -> Icons.Default.SentimentDissatisfied
                                "심한 불균형" -> Icons.Default.SentimentVeryDissatisfied
                                else -> Icons.Default.Warning
                            },
                            tint = when (analyzedFigures?.neckFigure?.second) {
                                "정상" -> Color.Green
                                "약간의 불균형" -> Color.Yellow
                                "심한 불균형" -> Color.Red
                                else -> Color.White
                            },
                            contentDescription = "진단 아이콘"
                        )
                        Icon(
                            imageVector =
                            when (analyzedFigures?.shoulderFigure?.second) {
                                "정상" -> Icons.Default.SentimentSatisfiedAlt
                                "약간의 불균형" -> Icons.Default.SentimentDissatisfied
                                "심한 불균형" -> Icons.Default.SentimentVeryDissatisfied
                                else -> Icons.Default.Warning
                            },
                            tint = when (analyzedFigures?.shoulderFigure?.second) {
                                "정상" -> Color.Green
                                "약간의 불균형" -> Color.Yellow
                                "심한 불균형" -> Color.Red
                                else -> Color.White
                            },
                            contentDescription = "진단 아이콘"
                        )
                        Icon(
                            imageVector =
                            when (analyzedFigures?.pelvisFigure?.second) {
                                "정상" -> Icons.Default.SentimentSatisfiedAlt
                                "약간의 불균형" -> Icons.Default.SentimentDissatisfied
                                "심한 불균형" -> Icons.Default.SentimentVeryDissatisfied
                                else -> Icons.Default.Warning
                            },
                            tint = when (analyzedFigures?.pelvisFigure?.second) {
                                "정상" -> Color.Green
                                "약간의 불균형" -> Color.Yellow
                                "심한 불균형" -> Color.Red
                                else -> Color.White
                            },
                            contentDescription = "진단 아이콘"
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(50.dp))
            Surface(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                color = md_theme_dark_onPrimaryContainer
            ) {
                Row(
                    modifier = Modifier
                        .clickable(
                            interactionSource = remember {
                                MutableInteractionSource()
                            },
                            indication = null,
                            onClick = {
                                scope.launch { isBottomSheetOpen = !isBottomSheetOpen }
                            }
                        )
                        .fillMaxSize(),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Column {
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(modifier = Modifier
                            .height(4.dp)
                            .width(150.dp)
                            .border(width = 2.dp, color = Color(0xFF48464F))

                        )
                        Text("하이")
                    }

                }
            }
        } else {
            Text("이미지를 처리 중입니다...")
        }
    }
    if (isBottomSheetOpen){
        StretchingBottomSheet(
            closeSheet = {isBottomSheetOpen = false},
            description = analyzedFigures!!
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StretchingBottomSheet(
    modifier: Modifier = Modifier,
    closeSheet : () -> Unit,
    description: UnbalanceFigureDto
){
    val sheetState = rememberModalBottomSheetState(
    )

    val stretchingScreenList =  mutableListOf<String>()

    ModalBottomSheet(
        modifier = Modifier.padding(16.dp),
        onDismissRequest = closeSheet,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        containerColor = md_theme_dark_onPrimaryContainer,
        dragHandle = {}
    ) {
        if(description.neckFigure.first.absoluteValue > 3){
            stretchingScreenList.add("FirstNeckStretching")
            stretchingScreenList.add("SecondNeckStretching")
        }
        if(description.shoulderFigure.first.absoluteValue > 3){
            stretchingScreenList.add("FirstShoulderStretching")
            stretchingScreenList.add("SecondShoulderStretching")
            stretchingScreenList.add("ThirdShoulderStretching")
        }
        if(description.pelvisFigure.first.absoluteValue > 3){
            stretchingScreenList.add("FirstPelvisStretching")
            stretchingScreenList.add("SecondPelvisStretching")
            stretchingScreenList.add("ThirdPelvisStretching")
        }

        val screenName = stretchingScreenList[Random.nextInt(stretchingScreenList.size)]
        Log.d("ReportScreen", screenName)
        when(screenName){
            "FirstNeckStretching" -> FirstNeckStretching()
            "SecondNeckStretching" -> SecondNeckStretching()
            "FirstShoulderStretching" -> FirstShoulderStretching()
            "SecondShoulderStretching" -> SecondShoulderStretching()
            "ThirdShoulderStretching" -> ThirdShoulderStretching()
            "FirstPelvisStretching" -> FirstPelvisStretching()
            "SecondPelvisStretching" -> SecondPelvisStretching()
            "ThirdPelvisStretching" -> ThirdPelvisStretching()
        }
    }
}




fun detectPose(
    imageFile: File,
    context: Context,
    onImageProcessed: (Uri, UnbalanceFigureDto) -> Unit,
){
    val poseDetectorOptions = AccuratePoseDetectorOptions.Builder()
        .setDetectorMode(AccuratePoseDetectorOptions.SINGLE_IMAGE_MODE)
        .build()

    val poseDetector = PoseDetection.getClient(poseDetectorOptions)

    val image = InputImage.fromFilePath(context, Uri.fromFile(imageFile))
    poseDetector.process(image)
        .addOnSuccessListener { pose ->
            val poseLandmarks = pose.allPoseLandmarks
            val result = drawPointsOnImage(imageFile, poseLandmarks, context)
            val drawnImageFile = result.first
            val description = result.second

            onImageProcessed(Uri.fromFile(drawnImageFile), description)
        }
        .addOnFailureListener { e ->
            Log.e("PoseDetection", "Pose detection failed: ${e.message}")
            onImageProcessed(Uri.EMPTY, UnbalanceFigureDto(Pair(0.0f, ""), Pair(0.0f, ""), Pair(0.0f, "")))
        }
}

fun drawPointsOnImage(
    originalImageFile: File,
    poseLandmarks: List<PoseLandmark>,
    context: Context
): Pair<File,UnbalanceFigureDto> {
    val bitmap = BitmapFactory.decodeFile(originalImageFile.absolutePath)

    val exif = ExifInterface(originalImageFile.absolutePath)
    val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)

    val rotationAngle = when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> 90
        ExifInterface.ORIENTATION_ROTATE_180 -> 180
        ExifInterface.ORIENTATION_ROTATE_270 -> 270
        else -> 0
    }

    val rotatedBitmap = if (rotationAngle != 0) {
        val matrix = Matrix()
        matrix.postRotate(rotationAngle.toFloat())
        Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    } else {
        bitmap
    }

    val mutableBitmap = rotatedBitmap.copy(Bitmap.Config.ARGB_8888, true)

    val canvas = Canvas(mutableBitmap)

    val paint = Paint().apply {
        color = ContextCompat.getColor(context, android.R.color.background_light)
        style = Paint.Style.FILL
        strokeWidth = 6f
    }

    val mainLandmark : List<PoseLandmark> = listOf(
        poseLandmarks[7],
        poseLandmarks[8],
        poseLandmarks[11],
        poseLandmarks[12],
        poseLandmarks[23],
        poseLandmarks[24],
    )

    val descriptions = UnbalanceFigureDto(
        getDescription(getAngle(mainLandmark[0],mainLandmark[1])),
        getDescription(getAngle(mainLandmark[2],mainLandmark[3])),
        getDescription(getAngle(mainLandmark[4],mainLandmark[5]))
    )


    for (landmark in mainLandmark) {
        // landmarkType 7,8(귀), 11,12(어깨), 23,24(엉덩이)
        val landmarkPosition = landmark.position
        canvas.drawCircle(
            landmarkPosition.x,
            landmarkPosition.y,
            20f,
            paint
        )
    }
    for(i in 0..2){
        canvas.drawLine(
            mainLandmark[i*2].position.x,
            mainLandmark[i*2].position.y,
            mainLandmark[i*2+1].position.x,
            mainLandmark[i*2+1].position.y,
            paint
        )
    }

    val drawnImageFile = File(context.filesDir, "drawn_${originalImageFile.name}")
    try {
        val outputStream = FileOutputStream(drawnImageFile)
        mutableBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.flush()
        outputStream.close()
    } catch (e: FileNotFoundException) {
        Log.e("drawPointsOnImage", "File not found: ${e.message}")
    } catch (e: IOException) {
        Log.e("drawPointsOnImage", "Error accessing file: ${e.message}")
    }

    return Pair(drawnImageFile, descriptions)
}

suspend fun getScore(imageUri: String): Response<UploadImageResponse> {
    val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor(accessToken!!))
        .build()

    val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
        .build()

    val service = retrofit.create(ApiService::class.java)

    return service.getResult(imageUri)
}

