@echo off
chcp 65001 >nul
title Live Dashboard - 停止所有服务

echo ==================================================
echo     Live Dashboard 停止脚本
echo ==================================================
echo.

echo 正在停止所有服务...

:: 停止 Java (后端)
taskkill /f /im java.exe >nul 2>&1
echo [OK] 已停止后端 (Java)

:: 停止 Node.js (前端)
taskkill /f /im node.exe >nul 2>&1
echo [OK] 已停止前端 (Node.js)

:: 停止 Python (Agent)
taskkill /f /im python.exe >nul 2>&1
echo [OK] 已停止 Agent (Python)

echo.
echo ==================================================
echo     所有服务已停止
echo ==================================================
echo.
pause
