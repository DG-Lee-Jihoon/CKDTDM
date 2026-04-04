@echo off
:: =====================================================
:: start-server.bat — SỬA SERVER_ID VÀ PEERS THEO TỪNG MÁY
:: Máy 1 (192.168.1.226): SERVER_ID=1
:: Máy 2 (192.168.1.227): SERVER_ID=2
:: Máy 3 (192.168.1.223): SERVER_ID=3
:: =====================================================

:: set SERVER_ID=1
:: set PEERS=http://192.168.1.227:8081,http://192.168.1.223:8081

:: Máy 2 dùng:
:: set SERVER_ID=2
:: set PEERS=http://192.168.1.226:8081,http://192.168.1.223:8081

:: Máy 3 dùng:
 set SERVER_ID=3
 set PEERS=http://192.168.1.226:8081,http://192.168.1.227:8081

set JAR=server\target\parking-sync-server-1.0.0.jar

if not exist %JAR% (
    echo Building project...
    cd server
    mvn clean package -DskipTests
    cd ..
)

echo Khoi dong Server %SERVER_ID% tren port 8081...
echo Peers: %PEERS%
java -DSERVER_ID=%SERVER_ID% -DSYNC_PEERS="%PEERS%" -jar %JAR%
