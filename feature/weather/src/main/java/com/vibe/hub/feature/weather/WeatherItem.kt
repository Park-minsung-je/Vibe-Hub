package com.vibe.hub.feature.weather

data class WeatherItem(
    val baseDate: String,
    val baseTime: String,
    val category: String,
    val fcstDate: String,
    val fcstTime: String,
    val fcstValue: String,
    val obsrValue: String?, // [추가] 실황 실측값 대응
    val nx: Int,
    val ny: Int
)