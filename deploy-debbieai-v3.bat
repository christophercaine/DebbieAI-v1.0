@echo off
REM ============================================================
REM DebbieAI Complete Project Deployment Script v3
REM Includes: Core + Photos + Contacts + Jobs + Tasks + Estimates
REM Date: January 29, 2026
REM ============================================================

echo.
echo ========================================================
echo   DebbieAI Complete Deployment Script v3
echo   All 6 Modules: Core, Photos, Contacts, Jobs, Tasks, Estimates
echo ========================================================
echo.

set PROJECT_ROOT=C:\Users\Superuser\Desktop\AndroidProjects
set APP_SRC=%PROJECT_ROOT%\app\src\main\java\com\debbiedoesit\debbieai
set ZIP_EXTRACT=%PROJECT_ROOT%\debbieai-unified

if not exist "%ZIP_EXTRACT%" (
    echo ERROR: Please extract debbieai-unified.zip to %PROJECT_ROOT% first!
    pause
    exit /b 1
)

echo [1/7] Creating directory structure...

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

REM Jobs directories
mkdir "%APP_SRC%\jobs\viewmodel" 2>nul
mkdir "%APP_SRC%\jobs\ui\components" 2>nul
mkdir "%APP_SRC%\jobs\ui\screens" 2>nul
mkdir "%APP_SRC%\jobs\ui\navigation" 2>nul
mkdir "%APP_SRC%\jobs\data\local" 2>nul
mkdir "%APP_SRC%\jobs\data\repository" 2>nul

REM Tasks directories
mkdir "%APP_SRC%\tasks\viewmodel" 2>nul
mkdir "%APP_SRC%\tasks\ui\components" 2>nul
mkdir "%APP_SRC%\tasks\ui\screens" 2>nul
mkdir "%APP_SRC%\tasks\ui\navigation" 2>nul
mkdir "%APP_SRC%\tasks\data\local" 2>nul
mkdir "%APP_SRC%\tasks\data\repository" 2>nul

REM Estimates directories
mkdir "%APP_SRC%\estimates\viewmodel" 2>nul
mkdir "%APP_SRC%\estimates\ui\components" 2>nul
mkdir "%APP_SRC%\estimates\ui\screens" 2>nul
mkdir "%APP_SRC%\estimates\ui\navigation" 2>nul
mkdir "%APP_SRC%\estimates\data\local" 2>nul
mkdir "%APP_SRC%\estimates\data\repository" 2>nul

echo [2/7] Copying CORE module...
copy "%ZIP_EXTRACT%\core\database\DebbieDatabase.kt" "%APP_SRC%\core\database\" /Y
copy "%ZIP_EXTRACT%\core\ui\theme\Theme.kt" "%APP_SRC%\core\ui\theme\" /Y
copy "%ZIP_EXTRACT%\navigation\DebbieNavigation.kt" "%APP_SRC%\navigation\" /Y
copy "%ZIP_EXTRACT%\MainActivity.kt" "%APP_SRC%\" /Y

echo [3/7] Copying PHOTOS module...
copy "%ZIP_EXTRACT%\photos\viewmodel\*.kt" "%APP_SRC%\photos\viewmodel\" /Y
copy "%ZIP_EXTRACT%\photos\ui\components\*.kt" "%APP_SRC%\photos\ui\components\" /Y
copy "%ZIP_EXTRACT%\photos\ui\screens\*.kt" "%APP_SRC%\photos\ui\screens\" /Y
copy "%ZIP_EXTRACT%\photos\ui\navigation\*.kt" "%APP_SRC%\photos\ui\navigation\" /Y

echo [4/7] Copying CONTACTS module...
copy "%ZIP_EXTRACT%\contacts\viewmodel\*.kt" "%APP_SRC%\contacts\viewmodel\" /Y
copy "%ZIP_EXTRACT%\contacts\ui\components\*.kt" "%APP_SRC%\contacts\ui\components\" /Y
copy "%ZIP_EXTRACT%\contacts\ui\screens\*.kt" "%APP_SRC%\contacts\ui\screens\" /Y
copy "%ZIP_EXTRACT%\contacts\ui\navigation\*.kt" "%APP_SRC%\contacts\ui\navigation\" /Y
copy "%ZIP_EXTRACT%\contacts\data\local\*.kt" "%APP_SRC%\contacts\data\local\" /Y
copy "%ZIP_EXTRACT%\contacts\data\repository\*.kt" "%APP_SRC%\contacts\data\repository\" /Y
copy "%ZIP_EXTRACT%\contacts\data\sync\*.kt" "%APP_SRC%\contacts\data\sync\" /Y

echo [5/7] Copying JOBS module...
copy "%ZIP_EXTRACT%\jobs\viewmodel\*.kt" "%APP_SRC%\jobs\viewmodel\" /Y
copy "%ZIP_EXTRACT%\jobs\ui\components\*.kt" "%APP_SRC%\jobs\ui\components\" /Y
copy "%ZIP_EXTRACT%\jobs\ui\screens\*.kt" "%APP_SRC%\jobs\ui\screens\" /Y
copy "%ZIP_EXTRACT%\jobs\ui\navigation\*.kt" "%APP_SRC%\jobs\ui\navigation\" /Y
copy "%ZIP_EXTRACT%\jobs\data\local\*.kt" "%APP_SRC%\jobs\data\local\" /Y
copy "%ZIP_EXTRACT%\jobs\data\repository\*.kt" "%APP_SRC%\jobs\data\repository\" /Y

echo [6/7] Copying TASKS module...
copy "%ZIP_EXTRACT%\tasks\viewmodel\*.kt" "%APP_SRC%\tasks\viewmodel\" /Y
copy "%ZIP_EXTRACT%\tasks\ui\components\*.kt" "%APP_SRC%\tasks\ui\components\" /Y
copy "%ZIP_EXTRACT%\tasks\ui\screens\*.kt" "%APP_SRC%\tasks\ui\screens\" /Y
copy "%ZIP_EXTRACT%\tasks\ui\navigation\*.kt" "%APP_SRC%\tasks\ui\navigation\" /Y
copy "%ZIP_EXTRACT%\tasks\data\local\*.kt" "%APP_SRC%\tasks\data\local\" /Y
copy "%ZIP_EXTRACT%\tasks\data\repository\*.kt" "%APP_SRC%\tasks\data\repository\" /Y

echo [7/7] Copying ESTIMATES module...
copy "%ZIP_EXTRACT%\estimates\viewmodel\*.kt" "%APP_SRC%\estimates\viewmodel\" /Y
copy "%ZIP_EXTRACT%\estimates\ui\components\*.kt" "%APP_SRC%\estimates\ui\components\" /Y
copy "%ZIP_EXTRACT%\estimates\ui\screens\*.kt" "%APP_SRC%\estimates\ui\screens\" /Y
copy "%ZIP_EXTRACT%\estimates\ui\navigation\*.kt" "%APP_SRC%\estimates\ui\navigation\" /Y
copy "%ZIP_EXTRACT%\estimates\data\local\*.kt" "%APP_SRC%\estimates\data\local\" /Y
copy "%ZIP_EXTRACT%\estimates\data\repository\*.kt" "%APP_SRC%\estimates\data\repository\" /Y

echo Copying documentation...
copy "%ZIP_EXTRACT%\ARCHITECTURE.md" "%PROJECT_ROOT%\" /Y
copy "%ZIP_EXTRACT%\README.md" "%PROJECT_ROOT%\DEBBIEAI_README.md" /Y

echo.
echo ========================================================
echo   FILES COPIED SUCCESSFULLY!
echo ========================================================
echo.
echo Modules installed:
echo   [x] Core     - Database (16 entities), Theme, Navigation
echo   [x] Photos   - Gallery, Albums, Search, Favorites, Trash
echo   [x] Contacts - List, Detail, Add/Edit, Search, Duplicates  
echo   [x] Jobs     - Kanban, Timeline, Financials, Full Lifecycle
echo   [x] Tasks    - List, Calendar, Reminders, Categories
echo   [x] Estimates - Line Items, Templates, PDF-ready, Status Tracking
echo.
echo Total: ~65 Kotlin files across 6 modules
echo.
echo ========================================================
echo   DEPLOYING TO GITHUB
echo ========================================================
echo.

cd /d "%PROJECT_ROOT%"

echo Adding all files to git...
git add .

echo Creating commit...
git commit -m "DebbieAI v3: Complete 6-module deployment (Core, Photos, Contacts, Jobs, Tasks, Estimates)"

echo Pushing to GitHub...
git push

echo.
echo ========================================================
echo   DEPLOYMENT COMPLETE!
echo ========================================================
echo.
echo Repository: github.com/christophercaine/debbie-ai
echo.
echo Next Steps:
echo   1. Open Android Studio
echo   2. Sync Gradle (File ^> Sync Project with Gradle Files)
echo   3. Build ^> Rebuild Project
echo   4. Run on device/emulator
echo.
pause
