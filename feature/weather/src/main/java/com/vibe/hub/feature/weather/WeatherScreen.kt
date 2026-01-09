fun getUnit(category: String): String = when(category) {
    "REH", "POP" -> "%"
    "WSD" -> "m/s"
    "VEC" -> "°"
    else -> ""
}