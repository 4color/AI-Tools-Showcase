@echo off
chcp 65001 >nul
cd /d "%~dp0"

echo Starting AI Tools Showcase...

rem Start backend
echo [1/2] Starting backend (port 8080)...
start "AI Tools Backend" cmd /k "set PATH=C:\Users\Administrator\.jdks\corretto-1.8.0_422\bin;C:\dev\apache-maven-3.9.8\bin;%%PATH%% && cd /d %~dp0backend && mvn spring-boot:run -Dspring-boot.run.profiles=dev -s C:\dev\apache-maven-3.9.8\conf\settings-2023.xml"

rem Wait for backend
echo Waiting for backend to start...
ping -n 10 127.0.0.1 >nul

rem Start frontend
echo [2/2] Starting frontend (port 3000)...
cd /d "%~dp0frontend"
if not exist "node_modules" (
    echo Installing dependencies...
    call npm install
)
start "AI Tools Frontend" cmd /k "npm run dev"

echo.
echo ==============================
echo Started successfully!
echo Frontend: http://localhost:3000
echo Backend: http://localhost:8080
echo ==============================
echo.
pause
