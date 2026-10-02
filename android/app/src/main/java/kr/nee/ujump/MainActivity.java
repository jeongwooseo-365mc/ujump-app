package kr.nee.ujump;

import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;
import android.webkit.WebView;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.getcapacitor.BridgeActivity;
import com.getcapacitor.WebViewListener;

import java.util.Locale;

public class MainActivity extends BridgeActivity {
    // 카메라 구멍(디스플레이 컷아웃) 크기를 웹 페이지 CSS 변수(--safe-l/r/t/b)로 넘기는 스크립트
    private String safeAreaJs = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 게임 중 화면이 꺼지지 않게
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // 화면 끝까지(카메라 구멍 영역 포함) 그리기. 비는 곳이 생기지 않게 창 배경은 어둡게
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            getWindow().setAttributes(lp);
        }
        getWindow().getDecorView().setBackgroundColor(0xFF0A2A6B);
        hideSystemBars();

        // 3D 화면은 구멍 영역까지 채우고, 버튼/글자는 사이트 CSS 가 이 값만큼 안쪽에 배치
        WebView web = bridge.getWebView();
        ViewCompat.setOnApplyWindowInsetsListener(web, (v, insets) -> {
            Insets c = insets.getInsets(WindowInsetsCompat.Type.displayCutout());
            float d = getResources().getDisplayMetrics().density;
            safeAreaJs = String.format(Locale.US,
                "(function(s){s.setProperty('--safe-l','%.1fpx');s.setProperty('--safe-r','%.1fpx');s.setProperty('--safe-t','%.1fpx');s.setProperty('--safe-b','%.1fpx');})(document.documentElement.style)",
                c.left / d, c.right / d, c.top / d, c.bottom / d);
            applySafeArea();
            return ViewCompat.onApplyWindowInsets(v, insets);
        });
        // 페이지가 새로 열릴 때마다(서버 보안 확인 후 이동 포함) 다시 적용
        bridge.addWebViewListener(new WebViewListener() {
            @Override
            public void onPageLoaded(WebView webView) {
                applySafeArea();
            }
        });
        ViewCompat.requestApplyInsets(web);
    }

    private void applySafeArea() {
        if (bridge == null || safeAreaJs.isEmpty()) return;
        WebView w = bridge.getWebView();
        if (w != null) w.post(() -> w.evaluateJavascript(safeAreaJs, null));
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) hideSystemBars();
    }

    // 전체 화면: 상태바/내비게이션바 숨김 (화면 가장자리를 쓸어내리면 잠깐 나타남)
    private void hideSystemBars() {
        WindowInsetsControllerCompat c = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        c.hide(WindowInsetsCompat.Type.systemBars());
        c.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
    }
}
