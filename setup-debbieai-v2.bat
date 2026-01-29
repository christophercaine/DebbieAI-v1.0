@echo off
REM DebbieAI Setup Script v2 - 42 Kotlin files

echo ========================================
echo  DebbieAI Project Setup v2
echo ========================================

set PROJECT_ROOT=C:\Users\Superuser\Desktop\AndroidProjects
set APP_SRC=%PROJECT_ROOT%\app\src\main\java\com\debbiedoesit\debbieai
set ZIP_EXTRACT=%PROJECT_ROOT%\debbieai-unified

if not exist "%ZIP_EXTRACT%" (
    echo ERROR: Extract debbieai-unified.zip first!
    pause
    exit /b 1
)

echo Creating directories...
for %%d in (core\database core\ui\theme navigation) do mkdir "%APP_SRC%\%%d" 2>nul
for %%d in (viewmodel ui\components ui\screens ui\navigation data\local data\repository data\sync) do mkdir "%APP_SRC%\contacts\%%d" 2>nul
for %%d in (viewmodel ui\components ui\screens ui\navigation) do mkdir "%APP_SRC%\photos\%%d" 2>nul
for %%d in (viewmodel ui\components ui\screens ui\navigation data\local data\repository) do mkdir "%APP_SRC%\jobs\%%d" 2>nul

echo Copying files...
xcopy "%ZIP_EXTRACT%\core" "%APP_SRC%\core" /E /Y /Q
xcopy "%ZIP_EXTRACT%\contacts" "%APP_SRC%\contacts" /E /Y /Q
xcopy "%ZIP_EXTRACT%\photos" "%APP_SRC%\photos" /E /Y /Q
xcopy "%ZIP_EXTRACT%\jobs" "%APP_SRC%\jobs" /E /Y /Q
xcopy "%ZIP_EXTRACT%\navigation" "%APP_SRC%\navigation" /E /Y /Q
copy "%ZIP_EXTRACT%\MainActivity.kt" "%APP_SRC%\" /Y
copy "%ZIP_EXTRACT%\*.md" "%PROJECT_ROOT%\" /Y

echo.
echo Done! 42 files copied.
echo.
echo Next: Add to build.gradle.kts:
echo   implementation("io.coil-kt:coil-compose:2.5.0")
echo   implementation("androidx.navigation:navigation-compose:2.7.6")
echo.
echo Then: git add . ^&^& git commit -m "Add Jobs module" ^&^& git push
pause
