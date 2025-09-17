@echo off
title 创建 VCampus 独立 exe 文件
echo.
echo ========================================
echo   创建 VCampus 独立可执行 exe 文件
echo ========================================
echo.

cd /d "H:\vcampus\vcampus-client"

REM 检查 JLink 应用程序是否存在
if not exist "target\vcampus-client\bin\vcampus-client" (
    echo JLink 应用程序不存在，正在创建...
    call mvn clean compile package javafx:jlink
    if %ERRORLEVEL% neq 0 (
        echo 创建 JLink 应用程序失败！
        pause
        exit /b 1
    )
)

echo ✓ JLink 应用程序已准备就绪
echo.

REM 使用 JPackage 创建独立的应用程序包（不是安装程序）
echo 正在创建独立 exe 文件...

REM 创建输出目录
if not exist "target\standalone" mkdir "target\standalone"

REM 复制主jar文件到lib目录
copy "target\vcampus-client-1.0-SNAPSHOT.jar" "target\lib\" >nul 2>&1

REM 使用 JPackage 创建应用程序包
jpackage ^
    --type app-image ^
    --name "VCampus-Client" ^
    --app-version "1.0.0" ^
    --vendor "VCampus Team" ^
    --description "VCampus 智慧校园客户端" ^
    --main-jar "vcampus-client-1.0-SNAPSHOT.jar" ^
    --main-class "com.vcampus.client.MainApp" ^
    --input "target\lib" ^
    --runtime-image "target\vcampus-client" ^
    --dest "target\standalone"

if %ERRORLEVEL% neq 0 (
    echo 创建失败！请确保使用 JDK 17+ 版本。
    pause
    exit /b 1
)

echo.
echo ========================================
echo           🎉 创建成功！
echo ========================================
echo.
echo 独立 exe 文件位置:
echo target\standalone\VCampus-Client\VCampus-Client.exe
echo.
echo 这是一个真正的 Windows exe 文件：
echo ✓ 双击直接运行，无需安装
echo ✓ 包含完整的 Java 运行时环境
echo ✓ 包含所有依赖库和资源文件
echo ✓ 可以复制到任何 Windows 电脑上运行
echo.
echo 使用方法：
echo 1. 双击 VCampus-Client.exe 直接运行
echo 2. 或将整个 VCampus-Client 文件夹复制给其他人
echo.

echo.
pause
