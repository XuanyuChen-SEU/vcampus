@echo off
REM VCampus 客户端快速启动脚本
REM 假设JAR包和JavaFX SDK在同一个目录下

set JAR_NAME=vcampus-client-1.0-SNAPSHOT.jar
set JAVAFX_PATH=javafx-sdk-21.0.8\lib

REM 获取脚本所在目录
set SCRIPT_DIR=%~dp0
set JAR_PATH=%SCRIPT_DIR%%JAR_NAME%
set JAVAFX_FULL_PATH=%SCRIPT_DIR%%JAVAFX_PATH%

echo 启动VCampus客户端...

REM 检查文件是否存在
if not exist "%JAR_PATH%" (
    echo 错误: 找不到JAR文件: %JAR_NAME%
    pause
    exit /b 1
)

if not exist "%JAVAFX_FULL_PATH%" (
    echo 错误: 找不到JavaFX SDK: %JAVAFX_PATH%
    pause
    exit /b 1
)

REM 启动应用程序
java --module-path "%JAVAFX_FULL_PATH%" --add-modules javafx.controls,javafx.fxml,javafx.web -jar "%JAR_PATH%"

pause
