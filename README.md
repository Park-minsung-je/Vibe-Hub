# VibeHub 📱

**"Your Daily Vibe, Perfectly Hubbed."**

VibeHub는 일상의 모든 바이브를 하나로 연결하는 안드로이드 통합 허브 애플리케이션입니다.  
현재는 고도화된 **Vibe Weather** 서비스를 통해 가장 정확하고 아름다운 날씨 경험을 제공합니다.

---

## ✨ Key Features

### 1. Ultra-Precision Weather (초정밀 날씨)
*   **3-Layer Fusion Data**: 기상청의 **초단기실황(실측)**, **초단기예보**, **단기예보** 3종 API를 서버에서 정교하게 융합하여, '지금 당장'의 날씨를 가장 정확하게 보여줍니다.
*   **Smart Location**: 카카오 로컬 API와 연동하여 현재 위치의 정확한 행정구역 주소("📍 서울특별시 강남구 역삼동")를 표시합니다.
*   **Air Quality**: 미세먼지(PM10)와 초미세먼지(PM2.5) 정보를 실시간 등급 컬러와 함께 제공합니다.

### 2. Luxury UI/UX (고품격 디자인)
*   **Glassmorphism & Blur**: 새로고침 시 화면이 사라지는 대신, 은은한 블러(Blur) 효과가 적용되어 끊김 없는 경험을 선사합니다.
*   **Snap Toolbar**: 스크롤 방향에 따라 부드럽게 사라지고 나타나는 반응형 상단바를 구현했습니다.
*   **Off-beat Animation**: 뒤로가기 버튼과 타이틀이 서로 다른 템포로 움직이는 정교한 애니메이션 디테일을 적용했습니다.
*   **Minimalism**: 불필요한 섹션 타이틀을 제거하고, 정보의 밀도와 폰트 위계를 조절하여 오직 데이터에만 집중할 수 있습니다.

### 3. Intelligent Caching (스마트 캐싱)
*   **Singleton Repository Cache**: 화면을 이탈했다 돌아와도 네트워크 재요청 없이 **즉시(Instant)** 데이터를 표시합니다.
*   **Pull-to-Refresh**: 사용자가 명시적으로 당겨서 새로고침할 때만 최신 데이터를 가져와 데이터 낭비를 막습니다.
*   **Animation Memory**: 화려한 등장 애니메이션은 최초 1회만 실행되어 시각적 피로도를 낮췄습니다.

---

## 🛠️ Tech Stack

*   **Language**: Kotlin 100%
*   **UI Framework**: Jetpack Compose (Material3)
*   **Architecture**: MVVM + Clean Architecture
*   **Dependency Injection**: Hilt
*   **Network**: Retrofit2 + OkHttp3
*   **Async**: Coroutines + Flow
*   **Graphics**: Canvas Custom Drawing (Shadows, Blur)

---

## 📸 Screenshots

| <img src="https://via.placeholder.com/300x600?text=Home" width="200" /> | <img src="https://via.placeholder.com/300x600?text=Weather" width="200" /> | <img src="https://via.placeholder.com/300x600?text=Detail" width="200" /> |
|:---:|:---:|:---:|
| **Home Hub** | **Vibe Weather** | **Detail View** |

---

## 🚀 Getting Started

### Prerequisites
*   Android Studio Ladybug (or newer)
*   JDK 17+
*   Android SDK 34+

### Installation
1.  Clone the repository:
    ```bash
    git clone https://github.com/Park-minsung-je/Vibe-Hub.git
    ```
2.  Open in Android Studio.
3.  Sync Gradle and Run on Emulator/Device.

---

## 🤝 Contribution

VibeHub는 오픈 소스 프로젝트가 아니며, 개인 포트폴리오 및 학습 목적으로 개발되었습니다.
이슈 리포팅 및 피드백은 언제나 환영합니다!

---

**Developed with ❤️ by Vibe Coding Team (w/ Gemini)**
