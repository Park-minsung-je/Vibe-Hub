package com.vibe.hub.feature.weather

data class AirQualityItem(
    val dataTime: String?,
    val stationName: String?,
    val khaiValue: String?, // 통합대기환경지수
    val khaiGrade: String?, // 통합대기환경지수 등급
    val pm10Value: String?, // 미세먼지 농도
    val pm10Grade: String?, // 미세먼지 등급
    val pm25Value: String?, // 초미세먼지 농도
    val pm25Grade: String?, // 초미세먼지 등급
    val no2Value: String?,
    val o3Value: String?,
    val coValue: String?,
    val so2Value: String?
)
