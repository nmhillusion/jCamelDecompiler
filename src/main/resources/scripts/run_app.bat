@echo off
setlocal enabledelayedexpansion

:: 1. Define local JDK path (current directory \ jdk \ bin \ java.exe)
set "LOCAL_JAVA=%~dp0jdk\bin\java.exe"

:: 2. Check local folder FIRST, then fall back to system PATH
if exist "%LOCAL_JAVA%" (
    set "JAVA_CMD="%LOCAL_JAVA%""
    echo [INFO] Found local JDK in: %~dp0jdk
) else (
    where java >nul 2>nul
    if !errorlevel! equ 0 (
        set "JAVA_CMD=java"
        echo [INFO] Local JDK not found. Using system PATH.
    ) else (
        echo [ERROR] Java was not found in the local \jdk\ folder OR system PATH.
        pause
        exit /b 1
    )
)

:: 3. Capture the version string
for /f "tokens=3" %%g in ('%JAVA_CMD% -version 2^>^&1 ^| findstr /i "version"') do (
    set "full_version=%%g"
)

:: 4. Clean up quotes and extract the major version
set "full_version=%full_version:"=%"
for /f "delims=." %%v in ("%full_version%") do (
    set "major_version=%%v"
)

:: 5. Output results
echo Java detected: %full_version%; major version: %major_version%

if "%major_version%" neq "21" (
    echo [!] Warning: You are on Java %major_version%. This is not Java 25. Java 25 is required to run this app.
    exit /b 1
)

:: 6. SET ENVIRONMENT FOR SECOND SCRIPT
:: We pass the specific java path as a variable so the next script knows exactly what to use.
set "CURRENT_JAVA_EXE=%JAVA_CMD%"

:: 7. CALL THE NEXT SCRIPT
if exist "%~dp0run_app.bat" (
    echo [EXEC] Calling run_app.bat...
    call "%~dp0run_app.bat"
) else (
    echo [ERROR] run_app.bat not found in %~dp0
)

pause