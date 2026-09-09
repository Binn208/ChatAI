# Fast Direct Android SDK Setup for Windows
$ErrorActionPreference = "Continue"
$SdkDir = "D:\Android\Sdk"
$TempDir = "$env:TEMP\android_setup"

if (-not (Test-Path $SdkDir)) { New-Item -ItemType Directory -Path $SdkDir -Force | Out-Null }
if (-not (Test-Path $TempDir)) { New-Item -ItemType Directory -Path $TempDir -Force | Out-Null }

Write-Host "=== Setting up Android SDK at $SdkDir ==="

# 1. Platform-tools
if (-not (Test-Path "$SdkDir\platform-tools\adb.exe")) {
    Write-Host "Downloading platform-tools..."
    curl.exe -L -o "$TempDir\platform-tools.zip" "https://dl.google.com/android/repository/platform-tools-latest-windows.zip"
    Write-Host "Extracting platform-tools..."
    Expand-Archive -Path "$TempDir\platform-tools.zip" -DestinationPath "$SdkDir" -Force
    Remove-Item "$TempDir\platform-tools.zip" -Force -ErrorAction SilentlyContinue
}

# 2. Build-tools 34.0.0
$BuildToolsDir = "$SdkDir\build-tools\34.0.0"
if (-not (Test-Path "$BuildToolsDir\aapt2.exe")) {
    Write-Host "Downloading build-tools 34.0.0..."
    curl.exe -L -o "$TempDir\build-tools.zip" "https://dl.google.com/android/repository/build-tools_r34-windows.zip"
    Write-Host "Extracting build-tools 34.0.0..."
    $ExtractTemp = "$TempDir\build-tools-temp"
    if (Test-Path $ExtractTemp) { Remove-Item -Recurse -Force $ExtractTemp }
    Expand-Archive -Path "$TempDir\build-tools.zip" -DestinationPath $ExtractTemp -Force
    New-Item -ItemType Directory -Path "$SdkDir\build-tools" -Force | Out-Null
    Move-Item -Path "$ExtractTemp\android-14" -Destination $BuildToolsDir -Force
    Remove-Item -Recurse -Force $ExtractTemp -ErrorAction SilentlyContinue
    Remove-Item "$TempDir\build-tools.zip" -Force -ErrorAction SilentlyContinue
}

# 3. Platforms android-34
$PlatformDir = "$SdkDir\platforms\android-34"
if (-not (Test-Path "$PlatformDir\android.jar")) {
    Write-Host "Downloading platforms android-34..."
    curl.exe -L -o "$TempDir\platform-34.zip" "https://dl.google.com/android/repository/platform-34-ext7_r03.zip"
    Write-Host "Extracting platforms android-34..."
    $ExtractTemp = "$TempDir\platform-temp"
    if (Test-Path $ExtractTemp) { Remove-Item -Recurse -Force $ExtractTemp }
    Expand-Archive -Path "$TempDir\platform-34.zip" -DestinationPath $ExtractTemp -Force
    New-Item -ItemType Directory -Path "$SdkDir\platforms" -Force | Out-Null
    # Note: folder inside zip is android-34-ext7
    $ExtractedChild = (Get-ChildItem -Path $ExtractTemp -Directory)[0].FullName
    Move-Item -Path $ExtractedChild -Destination $PlatformDir -Force
    Remove-Item -Recurse -Force $ExtractTemp -ErrorAction SilentlyContinue
    Remove-Item "$TempDir\platform-34.zip" -Force -ErrorAction SilentlyContinue
}

# 4. Licenses
$LicenseDir = "$SdkDir\licenses"
if (-not (Test-Path $LicenseDir)) { New-Item -ItemType Directory -Path $LicenseDir -Force | Out-Null }
Set-Content -Path "$LicenseDir\android-sdk-license" -Value "24333f8a63b6825ea9c5514f83c2829b004d1fee`n84831b9409646a3e80447b73b8220750c0b86441`nd56f5187479451eabf01fb78af6dfcb131a6481e"
Set-Content -Path "$LicenseDir\android-sdk-preview-license" -Value "84831b9409646a3e80447b73b8220750c0b86441"

# 5. Environment variables
[Environment]::SetEnvironmentVariable("ANDROID_HOME", $SdkDir, "User")
[Environment]::SetEnvironmentVariable("ANDROID_SDK_ROOT", $SdkDir, "User")
$env:ANDROID_HOME = $SdkDir
$env:ANDROID_SDK_ROOT = $SdkDir

Write-Host "=== Android SDK setup completed successfully! ==="
