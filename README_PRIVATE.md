# VibeHub Private Configuration

이 문서는 공개 리포지토리(`README.md`)에 포함되지 않는 **민감한 설정 및 로컬 환경 정보**를 다룹니다.

## 🔑 주요 설정 파일 (.gitignore)

다음 파일들은 Git에 포함되지 않으므로, 로컬 환경에서 직접 생성하거나 설정해야 합니다.

### 1. `local.properties` (Android SDK)
안드로이드 SDK 경로 및 서명 키 정보를 관리합니다.

```properties
sdk.dir=/Users/YOUR_USER/Library/Android/sdk
```

### 2. `keystore.jks` (Signing Key)
릴리즈 빌드를 위한 서명 키 파일입니다. 안전한 곳에 보관하세요.

## 🌐 서버 연결 (NetworkModule)

`core/network` 모듈의 `NetworkModule.kt`에서 API 서버 주소를 변경할 수 있습니다.

```kotlin
// 기본값 (에뮬레이터 로컬호스트)
const val BASE_URL = "http://10.0.2.2:8080/"
```