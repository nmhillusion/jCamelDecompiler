@echo off
setlocal enabledelayedexpansion

:: 1. Define local JDK path (current directory \ jdk \ bin \ java.exe)
set "LOCAL_JAVA=%~dp0jdk\bin\java.exe"

:: 2. Check local folder FIRST, then fall back to system PATH
if exist "%LOCAL_JAVA%" (
    set "JAVA_CMD=%LOCAL_JAVA%"
    echo [INFO] Found local JDK in: %~dp0jdk
    echo Local java detected: !JAVA_CMD!
) else (
    where java >nul 2>nul
    if !errorlevel! equ 0 (
        set "JAVA_CMD=java"
        echo [INFO] Local JDK not found. Using system PATH.
    ) else (
        call :ShowError "[ERROR] Java was not found in the local \jdk\ folder OR system PATH." "JDK Error"
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

set "REQUIRED_JDK_VERSION=21"

if %major_version% LSS %REQUIRED_JDK_VERSION% (
    call :ShowError "[ERROR] You are on Java %full_version%. Java version %REQUIRED_JDK_VERSION% or higher is required to run this app." "Runtime Error"
    exit /b 1
)

:: 6. SET ENVIRONMENT FOR SECOND SCRIPT
:: We pass the specific java path as a variable so the next script knows exactly what to use.
set "CURRENT_JAVA_EXE=%JAVA_CMD%"

:: 7. CALL THE NEXT SCRIPT
set "app_name=jCamelDecompilerApp"
if exist "%~dp0%app_name%.bat" (
    echo [EXEC] Calling %~dp0%app_name%.bat ...
    call "%~dp0%app_name%"
    exit /b 0
) else (
    call :ShowError "[ERROR] %~dp0%app_name%.bat not found in %~dp0" "Error"
    exit /b 1
)

:: --- Function Definition ---
:ShowError
:: %~1 is the first argument (message)
:: %~2 is the second argument (title)
powershell -Command "Add-Type -AssemblyName System.Windows.Forms; [System.Windows.Forms.MessageBox]::Show('%~1', '%~2', 'OK', 'Error')"
goto :eof