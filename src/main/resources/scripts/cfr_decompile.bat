@echo off

set EXEC_LIB_PATH=%1
set CLASS_FILE=%2
set OUTPUT_FILE=%3

@REM Check if not set %CURRENT_JAVA_EXE%, then fallback %CURRENT_JAVA_EXE% to java.exe
IF NOT DEFINED CURRENT_JAVA_EXE (
    SET CURRENT_JAVA_EXE=java.exe
)

echo Executing on Java:
%CURRENT_JAVA_EXE% -version

@REM Run Decompiler to decompile the class file
%CURRENT_JAVA_EXE% -jar %EXEC_LIB_PATH% %CLASS_FILE% > %OUTPUT_FILE%
@REM java -jar $PROCYON_JAR $CLASS_FILE
