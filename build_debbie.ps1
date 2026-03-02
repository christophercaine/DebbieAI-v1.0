$ErrorActionPreference = "Stop"

Write-Host "🚀 Launching DebbieAI Build System..." -ForegroundColor Cyan

# Check if we are in the root and need to go deeper
if (Test-Path "DebbieAI") {
    Write-Host "📂 Found DebbieAI folder. Entering..." -ForegroundColor Gray
    Set-Location "DebbieAI"
}

if (!(Test-Path "gradlew.bat")) {
    Write-Error "❌ Could not find gradlew.bat. Are you in the right folder?"
    exit 1
}

# Set Java Home explicitly to the one we found earlier
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
Write-Host "☕ Using Java at: $env:JAVA_HOME" -ForegroundColor Gray

# Run the build
Write-Host "🔨 Building Debug APK..." -ForegroundColor Cyan
./gradlew.bat assembleDebug --no-daemon --project-cache-dir "C:\Users\Debbi\.gradle_project_cache"

if ($LASTEXITCODE -eq 0) {
    Write-Host "✅ Build Success!" -ForegroundColor Green
} else {
    Write-Error "❌ Build Failed."
}
