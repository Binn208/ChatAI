# Build APK Script for ChatAiAdr using Standalone Gradle 8.7
$ErrorActionPreference = "Continue"

$env:JAVA_HOME = "C:\Program Files\Java\jdk-17"
$env:ANDROID_HOME = "D:\Android\Sdk"
$env:ANDROID_SDK_ROOT = "D:\Android\Sdk"
$GradleBin = "D:\Android\gradle-8.7\bin\gradle.bat"

Write-Host "========================================="
Write-Host "  BUILDING CHAT AI ANDROID APK (DEBUG)   "
Write-Host "========================================="
Write-Host "JAVA_HOME:    $env:JAVA_HOME"
Write-Host "ANDROID_HOME: $env:ANDROID_HOME"
Write-Host "GRADLE:       $GradleBin"

& $GradleBin assembleDebug --stacktrace

$apkPath = ".\app\build\outputs\apk\debug\app-debug.apk"
if (Test-Path $apkPath) {
    $apkItem = Get-Item $apkPath
    $sizeMb = [math]::Round($apkItem.Length / 1MB, 2)
    Write-Host "`n>>> BUILD SUCCESSFUL! <<<" -ForegroundColor Green
    Write-Host "APK File: $($apkItem.FullName)" -ForegroundColor Cyan
    Write-Host "Size:     $sizeMb MB" -ForegroundColor Cyan
    Write-Host "`nFile APK san sang de upload len appetize.io/upload hoac appetize.io/apps!" -ForegroundColor Green
} else {
    Write-Host "`n>>> BUILD FAILED! Khong tim thay file APK. <<<" -ForegroundColor Red
}
