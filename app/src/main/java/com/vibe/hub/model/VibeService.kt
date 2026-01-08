package com.vibe.hub.model

data class VibeService(
    val id: String,
    val name: String,
    val description: String,
    val iconUrl: String,
    val webUrl: String,
    val isNativeSupported: Boolean = true
)

enum class LaunchMode {
    NATIVE, WEBVIEW
}
