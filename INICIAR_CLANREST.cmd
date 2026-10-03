@echo off
cd /d "%~dp0"
if not exist "runtime\bin\java.exe" (
 echo No se encontro el Java portable. Use JDK 21 segun README.
 pause
 exit /b 1
)
echo ClanRest Gestion - abrir http://127.0.0.1:8080
echo Para detener, presione Ctrl+C.
"runtime\bin\java.exe" -Duser.timezone=America/Lima -jar "clanrest-1.0.0.jar"
pause
