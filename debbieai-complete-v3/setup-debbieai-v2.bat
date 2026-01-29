@echo off
REM ============================================================
REM DebbieAI Unified Project Setup Script v2
REM Includes: Core + Photos + Contacts modules
REM ============================================================

echo.
echo ========================================
echo  DebbieAI Project Setup Script v2
echo ========================================
echo.

set PROJECT_ROOT=C:\Users\Superuser\Desktop\AndroidProjects
set APP_SRC=%PROJECT_ROOT%\app\src\main\java\com\debbiedoesit\debbieai
set ZIP_EXTRACT=%PROJECT_ROOT%\debbieai-unified

if not exist "%ZIP_EXTRACT%" (
    echo ERROR: Please extract debbieai-unified.zip to %PROJECT_ROOT% first!
    pause
    exit /b 1
)

echo Creating directory structure...

REM Core directories
mkdir "%APP_SRC%\core\database" 2>nul
mkdir "%APP_SRC%\core\ui\theme" 2>nul
mkdir "%APP_SRC%\navigation" 2>nul

REM Photos directories
mkdir "%APP_SRC%\photos\viewmodel" 2>nul
mkdir "%APP_SRC%\photos\ui\components" 2>nul
mkdir "%APP_SRC%\photos\ui\screens" 2>nul
mkdir "%APP_SRC%\photos\ui\navigation" 2>nul

REM Contacts directories
mkdir "%APP_SRC%\contacts\viewmodel" 2>nul
mkdir "%APP_SRC%\contacts\ui\components" 2>nul
mkdir "%APP_SRC%\contacts\ui\screens" 2>nul
mkdir "%APP_SRC%\contacts\ui\navigation" 2>nul
mkdir "%APP_SRC%\contacts\data\local" 2>nul
mkdir "%APP_SRC%\contacts\data\repository" 2>nul
mkdir "%APP_SRC%\contacts\data\sync" 2>nul

echo Copying core files...
copy "%ZIP_EXTRACT%\core\database\DebbieDatabase.kt" "%APP_SRC%\core\database\" /Y
copy "%ZIP_EXTRACT%\core\ui\theme\Theme.kt" "%APP_SRC%\core\ui\theme\" /Y
copy "%ZIP_EXTRACT%\navigation\DebbieNavigation.kt" "%APP_SRC%\navigation\" /Y
copy "%ZIP_EXTRACT%\MainActivity.kt" "%APP_SRC%\" /Y

echo Copying photos module...
copy "%ZIP_EXTRACT%\photos\viewmodel\*.kt" "%APP_SRC%\photos\viewmodel\" /Y
copy "%ZIP_EXTRACT%\photos\ui\components\*.kt" "%APP_SRC%\photos\ui\components\" /Y
copy "%ZIP_EXTRACT%\photos\ui\screens\*.kt" "%APP_SRC%\photos\ui\screens\" /Y
copy "%ZIP_EXTRACT%\photos\ui\navigation\*.kt" "%APP_SRC%\photos\ui\navigation\" /Y

echo Copying contacts module...
copy "%ZIP_EXTRACT%\contacts\viewmodel\*.kt" "%APP_SRC%\contacts\viewmodel\" /Y
copy "%ZIP_EXTRACT%\contacts\ui\components\*.kt" "%APP_SRC%\contacts\ui\components\" /Y
copy "%ZIP_EXTRACT%\contacts\ui\screens\*.kt" "%APP_SRC%\contacts\ui\screens\" /Y
copy "%ZIP_EXTRACT%\contacts\ui\navigation\*.kt" "%APP_SRC%\contacts\ui\navigation\" /Y
copy "%ZIP_EXTRACT%\contacts\data\local\*.kt" "%APP_SRC%\contacts\data\local\" /Y
copy "%ZIP_EXTRACT%\contacts\data\repository\*.kt" "%APP_SRC%\contacts\data\repository\" /Y
copy "%ZIP_EXTRACT%\contacts\data\sync\*.kt" "%APP_SRC%\contacts\data\sync\" /Y

echo Copying documentation...
copy "%ZIP_EXTRACT%\ARCHITECTURE.md" "%PROJECT_ROOT%\" /Y
copy "%ZIP_EXTRACT%\README.md" "%PROJECT_ROOT%\UNIFIED_README.md" /Y

echo.
echo ========================================
echo  Files copied successfully!
echo ========================================
echo.
echo Modules installed:
echo   [x] Core (Database, Theme, Navigation)
echo   [x] Photos (Gallery, Albums, Search, Favorites, Trash)
echo   [x] Contacts (List, Detail, Add/Edit, Search, Duplicates)
echo.
echo Total files: 28 Kotlin files
echo.
echo ========================================
echo  Next Steps:
echo ========================================
echo.
echo 1. Open Android Studio
echo 2. Add to build.gradle.kts:
echo    implementation("io.coil-kt:coil-compose:2.5.0")
echo 3. Sync Gradle
echo 4. git add . ^&^& git commit -m "Add Photos + Contacts UI modules" ^&^& git push
echo.
pause
