@echo off
chcp 936 >nul
echo AI Tools Showcase 环境检查
echo ==============================
echo.

rem 检查 Java
echo 检查 Java 环境...
java -version 2>&1 | findstr "version" >nul
if errorlevel 1 (
    echo 错误：Java 未安装或未配置环境变量
    echo 下载地址: https://adoptium.net/
    set JAVA_STATUS=错误
) else (
    for /f "tokens=3" %%a in ('java -version 2^>^&1 ^| findstr "version"') do (
        echo Java 版本: %%a
    )
    set JAVA_STATUS=正常
)
echo.

rem 检查 Maven
echo 检查 Maven 环境...
set MAVEN_HOME=C:\dev\apache-maven-3.9.8
"%MAVEN_HOME%\bin\mvn" -version >nul 2>&1
if errorlevel 1 (
    echo 错误：Maven 未找到，请检查路径 %MAVEN_HOME%
    set MAVEN_STATUS=错误
) else (
    for /f "tokens=3" %%a in ('"%MAVEN_HOME%\bin\mvn" -version ^| findstr "Apache Maven"') do (
        echo Maven 版本: %%a
        echo Maven 路径: %MAVEN_HOME%
    )
    set MAVEN_STATUS=正常
)
echo.

rem 检查 Node.js
echo 检查 Node.js 环境...
node --version >nul 2>&1
if errorlevel 1 (
    echo 错误：Node.js 未安装
    echo 下载地址: https://nodejs.org/
    set NODE_STATUS=错误
) else (
    for /f "tokens=1" %%a in ('node --version') do (
        echo Node.js 版本: %%a
    )
    set NODE_STATUS=正常
)
echo.

rem 检查 MySQL (可选)
echo 检查 MySQL 环境...
mysql --version >nul 2>&1
if errorlevel 1 (
    echo 警告：MySQL 客户端未找到 (假设已安装)
    echo 提示：请确保 MySQL 服务正在运行
    set MYSQL_STATUS=警告
) else (
    echo MySQL 客户端已找到
    for /f "tokens=3" %%a in ('mysql --version ^| findstr "Distrib"') do (
        echo MySQL 版本: %%a
    )
    set MYSQL_STATUS=正常
)
echo.

rem 检查端口占用
echo 检查端口占用情况...
netstat -ano | findstr :3000 >nul
if errorlevel 1 (
    echo 端口 3000 (前端) 可用
) else (
    echo 警告：端口 3000 已被占用
    for /f "tokens=5" %%a in ('netstat -ano ^| findstr :3000 ^| findstr LISTENING') do (
        echo    进程 PID: %%a
    )
)

netstat -ano | findstr :8080 >nul
if errorlevel 1 (
    echo 端口 8080 (后端) 可用
) else (
    echo 警告：端口 8080 已被占用
    for /f "tokens=5" %%a in ('netstat -ano ^| findstr :8080 ^| findstr LISTENING') do (
        echo    进程 PID: %%a
    )
)

echo.
echo ==============================
echo 环境检查总结：
echo    Java: %JAVA_STATUS%
echo    Maven: %MAVEN_STATUS%
echo    Node.js: %NODE_STATUS%
echo    MySQL: %MYSQL_STATUS%
echo ==============================

if "%JAVA_STATUS%"=="正常" if "%MAVEN_STATUS%"=="正常" if "%NODE_STATUS%"=="正常" (
    echo.
    echo 环境检查通过！可以运行 start-all-gbk.bat 启动项目
) else (
    echo.
    echo 部分环境需要配置，请根据上述提示安装缺失的软件
)

echo.
pause