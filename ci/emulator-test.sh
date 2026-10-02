#!/usr/bin/env bash
# 에뮬레이터에 APK 를 설치/실행하고 화면·WebView 상태·로그를 out/ 에 저장
set -x
mkdir -p out
adb install -r ujump.apk
adb logcat -c
adb shell settings put secure immersive_mode_confirmations confirmed   # 전체 화면 안내 팝업 끄기
adb shell am start -W -n kr.nee.ujump/.MainActivity
for t in 15 30 60; do
  sleep 15
  adb exec-out screencap -p > "out/screen_${t}s.png"
done
PID=$(adb shell pidof kr.nee.ujump | tr -d '\r')
echo "pid=$PID"
adb forward tcp:9222 "localabstract:webview_devtools_remote_${PID}"
node ci/cdp.mjs 9222 > out/webview_state.txt 2>&1
cat out/webview_state.txt
adb logcat -d > out/logcat_full.txt
grep -E "Capacitor|chromium|Console|WebView|AndroidRuntime|ujump" out/logcat_full.txt > out/logcat_app.txt || true
tail -n 80 out/logcat_app.txt
exit 0
