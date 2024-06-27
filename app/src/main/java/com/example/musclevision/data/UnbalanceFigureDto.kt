package com.example.musclevision.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UnbalanceFigureDto(
    val neckFigure : Pair<Float, String>,
    val shoulderFigure : Pair<Float, String>,
    val pelvisFigure : Pair<Float, String>
) : Parcelable
