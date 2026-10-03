@echo off
cd /d "%~dp0"
set "JAVA_HOME=%~dp0runtime"
call "%~dp0tools\maven\bin\mvn.cmd" -B "-Dmaven.repo.local=%~dp0maven-cache" clean verify
if errorlevel 1 (
 pause
 exit /b 1
)
copy /Y "target\clanrest-1.0.0.jar" "clanrest-1.0.0.jar"
pause
