@echo off
set CP=build\classes;lib\mysql-connector-j-9.6.0.jar

echo Khoi dong cac Server...
start "Server1" java -cp "%CP%" Server1.Server1
start "Server2" java -cp "%CP%" Server2.Server2
start "Server3" java -cp "%CP%" Server3.Server3
start "Server4" java -cp "%CP%" Server4.Server4
start "Server5" java -cp "%CP%" Server5.Server5

timeout /t 2 >nul

echo Khoi dong Client...
start "Client" java -cp "%CP%" Client1.Client
