@echo off
title RuPay - Presentation Mode
color 0A

echo ===================================================
echo   Starting RuPay Desktop App (Local JavaFX Mode)
echo ===================================================
echo.
echo Please wait while the application compiles and starts...
echo.

cd rupay-javafx
call .\mvnw.cmd clean javafx:run

echo.
echo Application closed.
pause