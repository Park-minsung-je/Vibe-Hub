package com.vibe.hub.data.api

import kr.hyosang.coordinate.CoordPoint
import kr.hyosang.coordinate.TransCoord
import javax.inject.Inject
import javax.inject.Singleton

/**
 * CoordPoint.jar 라이브러리를 사용하여 GPS(WGS84) 좌표를 TM 좌표로 변환합니다.
 * 에어코리아 측정소 찾기 등에서 활용됩니다.
 */
@Singleton
class CoordConverterService @Inject constructor() {

    fun convertWgs84ToTm(lat: Double, lon: Double): Pair<Double, Double> {
        val pt = CoordPoint(lon, lat)
        val tmPt = TransCoord.getTransCoord(pt, TransCoord.COORD_TYPE_WGS84, TransCoord.COORD_TYPE_TM)
        return Pair(tmPt.x, tmPt.y)
    }
}
