set EXEC_LIB_PATH=%1
set CLASS_FILE=%2
set OUTPUT_FILE=%3

@REM Run Decompiler to decompile the class file
%CURRENT_JAVA_EXE% -jar %EXEC_LIB_PATH% %CLASS_FILE% > %OUTPUT_FILE%
@REM java -jar $PROCYON_JAR $CLASS_FILE
