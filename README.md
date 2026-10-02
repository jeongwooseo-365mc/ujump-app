# 울주AI센터 점프 앱

게임 사이트(https://nee.n-e.kr/ujump/)를 여는 앱 껍데기입니다.
- **Windows:** Tauri (WebView2)
- **Android:** Capacitor

게임 본체는 사이트에 있습니다. 게임을 고쳐서 사이트에 올리면(`game` 폴더에서 `npm run deploy`) 앱을 다시 빌드할 필요가 없습니다.

## 게임 파일을 앱 안에 넣지 않는 이유
InfinityFree 서버는 브라우저가 아닌 요청에 보안 확인 페이지를 돌려줍니다. 그래서 파일을 앱에 넣으면 로그인과 기록 동기화 API를 쓸 수 없습니다. 앱이 사이트를 직접 열면 브라우저와 똑같이 동작합니다.

## 빌드
GitHub에 푸시하면 Actions가 자동으로 빌드합니다. 워크플로 파일은 `.github/workflows/build.yml`입니다.

| 결과물 | 아티팩트 → 파일 |
|---|---|
| Windows 설치파일 | `windows-installer` → `UljuAIJump_x.y.z_x64-setup.exe` |
| 안드로이드 | `android-apk` → `ujump.apk` |

- **안드로이드 APK:** 디버그 서명이라 직접 설치(사이드로드)만 됩니다. 플레이스토어에 올리려면 릴리스 키로 서명해야 합니다.
- **Releases:** `v1.0.0`처럼 태그를 푸시하면 두 파일이 Releases에도 올라갑니다.

## 파일 구성
| 경로 | 내용 |
|---|---|
| `src-tauri/` | Windows 앱. `tauri.conf.json`에 창 크기와 사이트 주소, F11로 전체 화면 전환 |
| `android/` | 안드로이드 프로젝트. 가로 고정, 화면 꺼짐 방지, 전체 화면. 카메라 구멍 크기를 사이트에 `--safe-*`로 전달 |
| `capacitor.config.json` | 안드로이드 앱 이름, 패키지 id(`kr.nee.ujump`), 사이트 주소 |
| `icon.png`, `assets/` | 아이콘과 스플래시 원본 (`art/icon/app-icon.png`에서 생성) |
| `web/` | 사이트에 연결할 수 없을 때 보이는 화면 |

## 아이콘 바꾸기
1. `icon.png`와 `assets/icon-only.png`를 교체합니다.
2. `npm run icons`를 실행합니다.
3. 안드로이드 적응형 아이콘 앞면(`mipmap-*/ic_launcher_foreground.png`)도 새 아이콘으로 다시 만들어야 합니다. `npm run icons`가 이 파일은 만들어 주지 않습니다.
