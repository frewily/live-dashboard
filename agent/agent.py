"""
Live Dashboard Agent - Windows 客户端
监控前台窗口活动并上报到后端服务器
使用 Python 内置库，无需安装额外依赖
"""

import ctypes
import time
import json
import platform
import argparse
import urllib.request
import urllib.error
from ctypes import wintypes

user32 = ctypes.windll.user32

CONFIG = {
    "server_url": "http://localhost:8080",
    "report_interval": 5,
    "device_id": None,
    "device_name": None,
}

APP_NAME_MAPPING = {
    "code": "VS Code",
    "code - insiders": "VS Code Insiders",
    "idea": "IntelliJ IDEA",
    "pycharm": "PyCharm",
    "webstorm": "WebStorm",
    "chrome": "Google Chrome",
    "msedge": "Microsoft Edge",
    "firefox": "Firefox",
    "explorer": "文件资源管理器",
    "cmd": "命令提示符",
    "powershell": "PowerShell",
    "windowsterminal": "Windows Terminal",
    "notepad": "记事本",
    "wechat": "微信",
    "qq": "QQ",
    "telegram": "Telegram",
    "discord": "Discord",
    "spotify": "Spotify",
    "cloudmusic": "网易云音乐",
    "qqmusic": "QQ音乐",
}


def get_foreground_window_title():
    """获取前台窗口标题"""
    hwnd = user32.GetForegroundWindow()
    if hwnd == 0:
        return ""
    
    length = user32.GetWindowTextLengthW(hwnd)
    if length == 0:
        return ""
    
    buffer = ctypes.create_unicode_buffer(length + 1)
    user32.GetWindowTextW(hwnd, buffer, length + 1)
    return buffer.value


def get_process_name_from_title(title):
    """从窗口标题推断应用名称"""
    if not title:
        return "idle"
    
    title_lower = title.lower()
    
    for key, name in APP_NAME_MAPPING.items():
        if key in title_lower:
            return name
    
    if "visual studio code" in title_lower or "vscode" in title_lower:
        return "VS Code"
    if "visual studio" in title_lower:
        return "Visual Studio"
    if "idea" in title_lower:
        return "IntelliJ IDEA"
    
    if " - " in title:
        parts = title.split(" - ")
        return parts[-1].strip() if parts else "Unknown"
    
    return "Unknown"


def get_device_id():
    """获取或生成设备唯一 ID"""
    if CONFIG["device_id"]:
        return CONFIG["device_id"]
    
    try:
        output = ctypes.create_unicode_buffer(64)
        size = wintypes.DWORD(64)
        ctypes.windll.advapi32.GetUserNameW(output, ctypes.byref(size))
        username = output.value
    except:
        username = "user"
    
    return f"{username}-{platform.node()}"


def get_device_name():
    """获取设备名称"""
    if CONFIG["device_name"]:
        return CONFIG["device_name"]
    return platform.node() or "My PC"


def report_activity(app_name, window_title):
    """上报活动到后端"""
    url = f"{CONFIG['server_url']}/api/report"
    
    headers = {
        "Content-Type": "application/json",
        "X-Device-Id": get_device_id(),
        "X-Device-Name": get_device_name(),
        "X-Platform": "windows",
    }
    
    payload = {
        "appId": app_name,
        "windowTitle": window_title,
        "timestamp": int(time.time() * 1000),
    }
    
    try:
        data = json.dumps(payload).encode("utf-8")
        req = urllib.request.Request(url, data=data, headers=headers, method="POST")
        with urllib.request.urlopen(req, timeout=5) as response:
            if response.status == 200:
                display_title = window_title[:50] + "..." if len(window_title) > 50 else window_title
                print(f"[OK] {app_name}: {display_title}")
            else:
                print(f"[ERR] HTTP {response.status}")
    except urllib.error.URLError as e:
        print(f"[ERR] {e.reason}")
    except Exception as e:
        print(f"[ERR] {e}")


def run_agent():
    """运行 Agent 主循环"""
    print("=" * 50)
    print("Live Dashboard Agent - Windows")
    print("=" * 50)
    print(f"Server: {CONFIG['server_url']}")
    print(f"Device: {get_device_name()} ({get_device_id()})")
    print(f"Interval: {CONFIG['report_interval']}s")
    print("=" * 50)
    print("Press Ctrl+C to stop")
    print()
    
    last_title = ""
    
    while True:
        try:
            title = get_foreground_window_title()
            
            if title != last_title:
                app_name = get_process_name_from_title(title)
                report_activity(app_name, title)
                last_title = title
            
            time.sleep(CONFIG["report_interval"])
            
        except KeyboardInterrupt:
            print("\nAgent stopped.")
            break
        except Exception as e:
            print(f"[ERR] {e}")
            time.sleep(5)


def main():
    parser = argparse.ArgumentParser(description="Live Dashboard Agent")
    parser.add_argument("--server", "-s", default="http://localhost:8080", help="Server URL")
    parser.add_argument("--interval", "-i", type=int, default=5, help="Report interval in seconds")
    parser.add_argument("--device-id", "-d", help="Device ID")
    parser.add_argument("--device-name", "-n", help="Device name")
    
    args = parser.parse_args()
    
    CONFIG["server_url"] = args.server
    CONFIG["report_interval"] = args.interval
    CONFIG["device_id"] = args.device_id
    CONFIG["device_name"] = args.device_name
    
    run_agent()


if __name__ == "__main__":
    main()
