@echo off
title FinanceApp

echo ========================================
echo    🚀 Iniciando FinanceApp
echo ========================================
echo.

echo 🐍 Iniciando Python Service...
cd python-service
start "Python Service" cmd /k "python app.py"
cd ..
echo ✅ Python Service iniciado
echo.

timeout /t 3 /nobreak > nul

echo ☕ Iniciando Spring Boot...
start "Spring Boot" cmd /k "mvn spring-boot:run"
echo ✅ Spring Boot iniciado
echo.

echo ========================================
echo ✅ Sistema iniciado!
echo 📱 Acesse: http://localhost:8082/pages/register.html
echo ========================================

timeout /t 5 /nobreak > nul
start http://localhost:8082/pages/register.html

pause