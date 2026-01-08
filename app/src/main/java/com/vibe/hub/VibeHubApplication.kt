package com.vibe.hub

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * @HiltAndroidApp: Hilt 의존성 주입의 시작점입니다.
 * 
 * Spring Boot의 @SpringBootApplication 어노테이션과 유사한 역할을 합니다.
 * 1. 이 어노테이션이 붙으면 Hilt가 애플리케이션 수준의 의존성 컨테이너를 생성합니다.
 * 2. 애플리케이션의 생명주기(Lifecycle)에 맞춰 의존성 주입 환경을 설정합니다.
 * 3. 모든 Hilt 컴포넌트의 부모 역할을 수행합니다.
 */
@HiltAndroidApp
class VibeHubApplication : Application()