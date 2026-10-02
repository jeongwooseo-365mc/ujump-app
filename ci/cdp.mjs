// 에뮬레이터 속 앱 WebView 에 개발자도구 프로토콜로 붙어서 현재 상태를 출력 (디버그 빌드 전용)
// 사용: node ci/cdp.mjs 9222
const port = process.argv[2] || '9222';
const pages = await (await fetch(`http://127.0.0.1:${port}/json`)).json();
console.log('pages:', JSON.stringify(pages.map(p => ({ type: p.type, url: p.url, title: p.title })), null, 1));
const page = pages.find(p => p.type === 'page') || pages[0];
if (!page) process.exit(0);
const ws = new WebSocket(page.webSocketDebuggerUrl);
let id = 0;
const pending = new Map();
ws.onmessage = (m) => { const d = JSON.parse(m.data); if (pending.has(d.id)) { pending.get(d.id)(d); pending.delete(d.id); } };
await new Promise(r => { ws.onopen = r; });
const send = (method, params = {}) => new Promise(r => { const i = ++id; pending.set(i, r); ws.send(JSON.stringify({ id: i, method, params })); });
const evaluate = async (expr) => (await send('Runtime.evaluate', { expression: expr, returnByValue: true })).result?.result?.value;
const state = await evaluate(`JSON.stringify({
  href: location.href, title: document.title, ready: document.readyState,
  cookie: document.cookie.replace(/=[^;]+/g, '=…'),
  scripts: [...document.scripts].map(s => s.src.split('/').pop() || '(inline)'),
  visibleScreens: [...document.querySelectorAll('.screen')].filter(s => !s.hidden).map(s => s.id),
  babylon: typeof BABYLON, net: typeof Net, capacitor: typeof Capacitor,
  safeL: document.documentElement.style.getPropertyValue('--safe-l'), safeR: document.documentElement.style.getPropertyValue('--safe-r'),
  viewport: innerWidth + 'x' + innerHeight, loginLeft: (document.getElementById('login') || document.body).getBoundingClientRect().left,
  bodyStart: document.body ? document.body.innerHTML.slice(0, 600) : null,
})`);
console.log('state:', state);
ws.close();
