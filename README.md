# Live Dashboard

实时设备活动仪表盘 - 公开展示你正在使用的应用，拥有二次元风格 UI 和隐私优先设计。

> **技术栈**: Vue 3 + Spring Boot 3 + Python Agent

## 致谢

本项目基于 [Monika-Dream/live-dashboard](https://github.com/Monika-Dream/live-dashboard) 进行重构实现。

原项目采用 **Bun + React + Next.js** 技术栈，本项目将其重构为 **Vue 3 + Spring Boot** 实现，保留了核心的访客追踪功能和二次元风格 UI。

感谢原作者的创意和开源贡献！

## 功能特性

- 🎀 猫耳装饰的视觉小说风格对话框
- 👀 实时访客追踪 - 显示"N 人在看喵~"
- 💻 真实设备状态监控 (Windows Agent)
- 🤖 机器人/爬虫过滤
- ⏱️ 60 秒超时自动清理离线设备
- 🔒 零依赖 Agent，使用 Python 内置库

## 项目原理

### 核心概念

这个项目做两件事：

| 功能 | 说明 |
|------|------|
| **访客追踪** | 记录有多少人在看页面 |
| **设备监控** | 显示你的电脑正在用什么软件 |

### 三层架构

```
┌─────────────────────────────────────────────────────────┐
│  Agent (Python) - 数据采集者                            │
│  👀 每 5 秒检查前台窗口，变了就上报给后端                 │
└─────────────────────────────────────────────────────────┘
                          ↓ POST /api/report
┌─────────────────────────────────────────────────────────┐
│  后端 (Spring Boot) - 数据管理者                        │
│  🧠 接收数据、存储数据、响应请求                         │
└─────────────────────────────────────────────────────────┘
                          ↑ GET /api/current
┌─────────────────────────────────────────────────────────┐
│  前端 (Vue 3) - 数据展示者                              │
│  📺 每 10 秒请求后端，更新页面显示                       │
└─────────────────────────────────────────────────────────┘
```

### 数据流向

```
Agent 采集数据 → 后端存储数据 → 前端展示数据
```

### 后端核心逻辑

#### 1. VisitorTracker - 访客计数器

```java
// 就像一个 Map，key 是访客 IP，value 是最后访问时间
Map<String, Long> seen = new ConcurrentHashMap<>();

// 有人访问时调用
public void heartbeat(String ip, String userAgent) {
    if (isBot(userAgent)) return;  // 过滤机器人
    seen.put(ip, System.currentTimeMillis());  // 记录时间
}

// 获取在线人数
public int getCount() {
    return seen.size();  // Map 有多少 key 就是多少人
}

// 每 30 秒清理超时的访客
@Scheduled(fixedRate = 30000)
public void cleanup() {
    // 删除 30 秒没心跳的记录
}
```

#### 2. DeviceStateService - 设备状态管理

```java
// 也是一个 Map，key 是设备 ID，value 是设备状态
Map<String, DeviceState> devices = new ConcurrentHashMap<>();

// Agent 上报时调用
public void updateDevice(DeviceState device) {
    device.setOnline(true);
    device.setLastSeenAt(System.currentTimeMillis());
    devices.put(device.getDeviceId(), device);
}

// 每 30 秒清理离线设备
@Scheduled(fixedRate = 30000)
public void cleanupOfflineDevices() {
    // 删除 60 秒没上报的设备
}
```

### 前端核心逻辑

```typescript
// 每 10 秒请求一次后端
onMounted(() => {
    fetchData()  // 立即请求一次
    setInterval(fetchData, 10000)  // 每 10 秒请求
})

// 请求后端 API
async function fetchData() {
    const data = await fetch('/api/current')
    viewerCount.value = data.viewerCount  // 更新访客数
    devices.value = data.devices          // 更新设备列表
}
```

### Agent 核心逻辑

```python
# 主循环
while True:
    # 获取当前窗口标题
    title = get_foreground_window_title()
    
    # 如果窗口变了，就上报
    if title != last_title:
        app_name = get_process_name_from_title(title)
        POST /api/report { appId: app_name, windowTitle: title }
        last_title = title
    
    # 等 5 秒再检查
    time.sleep(5)
```

### 定时任务汇总

| 任务 | 间隔 | 说明 |
|------|------|------|
| Agent 检查窗口 | 5 秒 | 检测窗口变化 |
| 前端轮询 | 10 秒 | 获取最新数据 |
| 后端清理访客 | 30 秒 | 删除超时的访客记录 |
| 后端清理设备 | 30 秒 | 删除离线的设备 |

### 为什么用 ConcurrentHashMap？

- **线程安全**: Spring Boot 多线程环境，多个请求可能同时访问
- **高性能**: 无锁读取，分段锁写入
- **适合场景**: 读多写少的缓存场景

## 项目结构

```
live-dashboard/
├── backend/                    # Spring Boot 后端
│   └── src/main/java/com/example/livedashboard/
│       ├── LiveDashboardApplication.java
│       ├── component/
│       │   └── VisitorTracker.java      # 访客追踪
│       ├── controller/
│       │   ├── CurrentController.java   # 状态查询 API
│       │   └── ReportController.java    # Agent 上报 API
│       ├── service/
│       │   └── DeviceStateService.java  # 设备状态管理
│       └── dto/
│
├── frontend/                   # Vue 3 前端
│   └── src/
│       ├── App.vue
│       ├── api.ts
│       ├── composables/
│       │   └── useDashboard.ts
│       └── components/
│           ├── Header.vue
│           ├── CurrentStatus.vue
│           └── DeviceCard.vue
│
├── agent/                      # Python Agent 客户端
│   ├── agent.py                # 零依赖，直接运行
│   └── requirements.txt
│
├── start.bat                   # Windows 一键启动脚本
├── stop.bat                    # Windows 一键停止脚本
│
└── README.md
```

## 快速开始

### 环境要求

| 工具 | 版本 |
|------|------|
| Java | 17+ |
| Node.js | 18+ |
| Python | 3.8+ |
| Maven | 3.8+ |

### 🚀 一键启动 (Windows)

双击运行 `start.bat` 即可自动启动所有服务：

```
start.bat    # 启动后端、前端、Agent
stop.bat     # 停止所有服务
```

启动后会自动打开浏览器访问 http://localhost:5173

### 手动启动

#### 1. 启动后端

```bash
cd backend
mvn spring-boot:run
```

后端将在 http://localhost:8080 启动

### 2. 启动前端

```bash
cd frontend
npm install
npm run dev
```

前端将在 http://localhost:5173 启动

### 3. 启动 Agent (Windows)

```bash
cd agent
python agent.py
```

> Agent 使用 Python 内置库，无需安装任何依赖！

### 4. 查看效果

1. 打开浏览器访问 http://localhost:5173
2. 观察显示的当前活动应用
3. 切换窗口，等待 5 秒后页面会更新

## Agent 使用说明

### 基本用法

```bash
python agent.py
```

### 自定义配置

```bash
# 指定服务器地址
python agent.py --server http://your-server:8080

# 指定上报间隔 (秒)
python agent.py --interval 10

# 指定设备名称
python agent.py --device-name "我的电脑"

# 完整示例
python agent.py -s http://localhost:8080 -i 5 -n "My PC"
```

### 命令行参数

| 参数 | 简写 | 默认值 | 说明 |
|------|------|--------|------|
| `--server` | `-s` | `http://localhost:8080` | 后端服务器地址 |
| `--interval` | `-i` | `5` | 上报间隔 (秒) |
| `--device-id` | `-d` | 自动生成 | 设备唯一 ID |
| `--device-name` | `-n` | 计算机名 | 设备显示名称 |

## API 接口

### GET /api/current

获取当前状态和访客数量。

**响应示例：**

```json
{
  "devices": [
    {
      "deviceId": "user-PCNAME",
      "deviceName": "My PC",
      "platform": "windows",
      "appName": "VS Code",
      "displayTitle": "agent.py — Visual Studio Code",
      "online": true,
      "lastSeenAt": 1699999999999
    }
  ],
  "serverTime": "2024-01-01T12:00:00.000Z",
  "viewerCount": 3
}
```

### POST /api/report

Agent 上报活动数据。

**请求头：**

| Header | 说明 |
|--------|------|
| `X-Device-Id` | 设备唯一 ID |
| `X-Device-Name` | 设备显示名称 |
| `X-Platform` | 平台 (windows/macOS/android) |

**请求体：**

```json
{
  "appId": "VS Code",
  "windowTitle": "agent.py — Visual Studio Code",
  "timestamp": 1699999999999
}
```

## 核心实现

### 后端：VisitorTracker.java

```java
@Component
public class VisitorTracker {
    private final ConcurrentHashMap<String, Long> seen = new ConcurrentHashMap<>();
    
    public void heartbeat(String ip, String userAgent) {
        if (isBot(userAgent)) return;  // 过滤机器人
        seen.put(ip, System.currentTimeMillis());
    }
    
    @Scheduled(fixedRate = 30000)
    public void cleanup() {
        long cutoff = System.currentTimeMillis() - 30000;
        seen.entrySet().removeIf(e -> e.getValue() < cutoff);
    }
    
    public int getCount() {
        return seen.size();
    }
}
```

### 后端：DeviceStateService.java

```java
@Service
public class DeviceStateService {
    private final ConcurrentHashMap<String, DeviceState> devices = new ConcurrentHashMap<>();
    
    public void updateDevice(DeviceState device) {
        device.setOnline(true);
        device.setLastSeenAt(System.currentTimeMillis());
        devices.put(device.getDeviceId(), device);
    }
    
    @Scheduled(fixedRate = 30000)
    public void cleanupOfflineDevices() {
        long cutoff = System.currentTimeMillis() - 60000;
        devices.entrySet().removeIf(e -> e.getValue().getLastSeenAt() < cutoff);
    }
}
```

### Agent：agent.py

```python
def get_foreground_window_title():
    """获取前台窗口标题"""
    hwnd = user32.GetForegroundWindow()
    length = user32.GetWindowTextLengthW(hwnd)
    buffer = ctypes.create_unicode_buffer(length + 1)
    user32.GetWindowTextW(hwnd, buffer, length + 1)
    return buffer.value

def report_activity(app_name, window_title):
    """上报活动到后端 (使用 Python 内置库)"""
    data = json.dumps(payload).encode("utf-8")
    req = urllib.request.Request(url, data=data, headers=headers, method="POST")
    with urllib.request.urlopen(req, timeout=5) as response:
        # ...
```

## 技术栈对比

| 维度 | 原版 (Bun + React) | 本版 (Spring Boot + Vue) |
|------|-------------------|-------------------------|
| 后端 | Bun + TypeScript | Java 17 + Spring Boot 3 |
| 前端 | React 19 + Next.js 15 | Vue 3.4 + Vite 5 |
| Agent | Python + requests | Python 内置库 (零依赖) |
| 数据库 | SQLite | 内存存储 |
| 启动速度 | ~10ms | ~2s |
| 内存占用 | ~20MB | ~200MB |

## 功能对比

| 功能 | 原版 | 本版 |
|------|------|------|
| 访客追踪 | ✅ | ✅ |
| 设备状态 | ✅ | ✅ |
| 时间线 | ✅ | ❌ |
| 健康数据 | ✅ | ❌ |
| 隐私分级 | ✅ | ❌ |
| 多设备 | ✅ | ✅ |

## 扩展计划

- [ ] 实现隐私分级系统
- [ ] 添加时间线功能
- [ ] 支持 SQLite 持久化
- [ ] macOS Agent 支持
- [ ] Android Agent 支持

## 隐私说明

- 所有数据存储在内存中，重启后丢失
- 不会将任何数据写入文件或数据库
- Agent 获取的窗口标题仅用于实时展示
- 适合个人使用，不建议部署到公网

## 许可证

MIT

## 原项目链接

- 原项目地址: https://github.com/Monika-Dream/live-dashboard
- 原作者: Monika-Dream
