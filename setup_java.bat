@echo off
echo Configurando JAVA_HOME para Android Studio...

REM Buscar Java en ubicaciones comunes de Android Studio
set "JAVA_PATH="

REM Verificar si Android Studio está instalado en ubicaciones comunes
if exist "C:\Program Files\Android\Android Studio\jbr" (
    set "JAVA_PATH=C:\Program Files\Android\Android Studio\jbr"
    echo Encontrado Java en: %JAVA_PATH%
) else if exist "C:\Program Files (x86)\Android\Android Studio\jbr" (
    set "JAVA_PATH=C:\Program Files (x86)\Android\Android Studio\jbr"
    echo Encontrado Java en: %JAVA_PATH%
) else if exist "%LOCALAPPDATA%\Android\Sdk\jbr" (
    set "JAVA_PATH=%LOCALAPPDATA%\Android\Sdk\jbr"
    echo Encontrado Java en: %JAVA_PATH%
) else (
    echo No se encontró Java. Buscando en PATH...
    where java >nul 2>&1
    if %errorlevel% == 0 (
        for /f "tokens=*" %%i in ('where java') do (
            set "JAVA_PATH=%%i"
            goto :found
        )
    )
    echo Error: No se pudo encontrar Java. Por favor instala Android Studio o Java.
    pause
    exit /b 1
)

:found
echo Configurando JAVA_HOME=%JAVA_PATH%
set "JAVA_HOME=%JAVA_PATH%"
set "PATH=%JAVA_HOME%\bin;%PATH%"

echo.
echo JAVA_HOME configurado correctamente!
echo JAVA_HOME=%JAVA_HOME%
echo.

REM Limpiar proyecto
echo Limpiando proyecto...
call gradlew.bat clean --no-daemon

echo.
echo Proyecto limpiado. Ahora puedes sincronizar en Android Studio.
pause
