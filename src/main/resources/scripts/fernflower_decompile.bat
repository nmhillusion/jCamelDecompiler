set EXEC_LIB_PATH=%1
set CLASS_FILE=%2
set OUTPUT_FILE=%3

@REM Get directory of OUTPUT_FILE
for %%I in ("%OUTPUT_FILE%") do (
    set "OUTPUT_DIR=%%~dpI"
)

echo "Executing on Java: "
%CURRENT_JAVA_EXE% -version

@REM Run Decompiler to decompile the class file
%CURRENT_JAVA_EXE% -jar %EXEC_LIB_PATH% %CLASS_FILE% %OUTPUT_DIR%

@REM ============ debug start: uncomment three lines below if you want to debug: run command and waiting for you to check
@REM start /wait "" cmd.exe /c "java -jar %EXEC_LIB_PATH% %CLASS_FILE% %OUTPUT_DIR% & pause"
@REM echo JAR has finished. Exiting batch file.
@REM exit /b %errorlevel%
@REM ============ debug end

