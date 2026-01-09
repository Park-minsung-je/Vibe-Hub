package com.vibe.hub.core.network

import kr.hyosang.coordinate.CoordPoint
import kr.hyosang.coordinate.TransCoord
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CoordConverterService @Inject constructor() {
    fun convertWgs84ToTm(lat: Double, lon: Double): Pair<Double, Double> {
        val pt = CoordPoint(lon, lat)
        val tmPt = TransCoord.getTransCoord(pt, TransCoord.COORD_TYPE_WGS84, TransCoord.COORD_TYPE_TM)
        return Pair(tmPt.x, tmPt.y)
    }
}