$ErrorActionPreference = "Stop"

Write-Host "Launching DebbieAI Build System..." -ForegroundColor Cyan

# Set location to the new fixed path
Set-Location "C:\Antigravity\DebbieAI"

if (!(Test-Path "gradlew.bat")) {
    Write-Error "Could not find gradlew.bat. Are you in the right folder?"
    exit 1
}

# Set Java Home explicitly
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
Write-Host "Using Java at: $env:JAVA_HOME" -ForegroundColor Gray

# Run the build
Write-Host "Building Debug APK..." -ForegroundColor Cyan
# We use .gradle_local to avoid OneDrive file locking issues
./gradlew.bat assembleDebug --no-daemon --project-cache-dir "C:\Users\Debbi\.gradle_project_cache"

if ($LASTEXITCODE -eq 0) {
    Write-Host "Build Success!" -ForegroundColor Green
}
else {
    Write-Error "Build Failed. Check the output above."
}
