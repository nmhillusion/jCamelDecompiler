set EXEC_LIB_PATH=%1
set CLASS_FILE=%2
set OUTPUT_FILE=%3

@REM Get directory of OUTPUT_FILE
for %%I in ("%OUTPUT_FILE%") do (
    set "OUTPUT_DIR=%%~dpI"
)

@REM Run Decompiler to decompile the class file
java -jar %EXEC_LIB_PATH% %CLASS_FILE% %OUTPUT_DIR%
@REM java -jar $PROCYON_JAR $CLASS_FILE
