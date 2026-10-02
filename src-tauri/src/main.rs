// 릴리스 빌드에서 콘솔 창을 띄우지 않음
#![cfg_attr(not(debug_assertions), windows_subsystem = "windows")]

// 창은 tauri.conf.json 에 적힌 게임 사이트(https://nee.n-e.kr/ujump/)를 엽니다
fn main() {
    tauri::Builder::default()
        .run(tauri::generate_context!())
        .expect("앱을 실행하지 못했습니다");
}
