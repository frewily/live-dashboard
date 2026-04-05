@echo off
chcp 65001 >nul
title Live Dashboard - 启动器

echo ==================================================
echo     Live Dashboard 一键启动脚本
echo ==================================================
echo.

:: 获取脚本所在目录
cd /d "%~dp0"

:: 检查 Java
where java >nul 2>&1
if %errorlevel% neq 0 (
    echo [错误] 未找到 Java，请先安装 Java 17+
    pause
    exit /b 1
)

:: 检查 Node.js
where node >nul 2>&1
if %errorlevel% neq 0 (
    echo [错误] 未找到 Node.js，请先安装 Node.js 18+
    pause
    exit /b 1
)

:: 检查 Python
where python >nul 2>&1
if %errorlevel% neq 0 (
    echo [警告] 未找到 Python，Agent 将不会启动
    set PYTHON_OK=0
) else (
    set PYTHON_OK=1
)

echo [1/3] 启动后端 (Spring Boot)...
start "Live Dashboard - Backend" cmd /k "cd /d "%~dp0backend" && mvn spring-boot:run"

echo 等待后端启动 (15秒)...
timeout /t 15 /nobreak >nul

echo [2/3] 启动前端 (Vue 3)...
start "Live Dashboard - Frontend" cmd /k "cd /d "%~dp0frontend" && npm run dev"

echo 等待前端启动 (5秒)...
timeout /t 5 /nobreak >nul

if %PYTHON_OK% equ 1 (
    echo [3/3] 启动 Agent (Python)...
    start "Live Dashboard - Agent" cmd /k "cd /d "%~dp0agent" && python agent.py"
) else (
    echo [3/3] 跳过 Agent (Python 未安装)
)

echo.
echo ==================================================
echo     所有服务已启动！
echo ==================================================
echo.
echo 后端:   http://localhost:8080
echo 前端:   http://localhost:5173
echo.
echo 按任意键打开浏览器访问前端...
pause >nul

start http://localhost:5173

echo.
echo 脚本已完成，你可以关闭此窗口。
echo 各服务将在独立的命令行窗口中运行。
echo.
