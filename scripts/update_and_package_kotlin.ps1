$workspace = (Split-Path -Parent $PSScriptRoot)
$downloads = [System.IO.Path]::Combine($env:USERPROFILE, "Downloads")
$desktop = [System.Environment]::GetFolderPath("Desktop")
$folderCaiDat = Join-Path $workspace "00_FILE_CAI_DAT_CHO_BAN"
$newApk = Join-Path $workspace "app\build\outputs\apk\release\app-release.apk"

Write-Host "Cap nhat file APK moi..." -ForegroundColor Cyan
Copy-Item $newApk (Join-Path $workspace "ChatAi_Moi_Nhat.apk") -Force
Copy-Item $newApk (Join-Path $workspace "ChatAiAdr-release.apk") -Force
Copy-Item $newApk (Join-Path $downloads "ChatAi_Moi_Nhat.apk") -Force
Copy-Item $newApk (Join-Path $downloads "ChatAiAdr-release.apk") -Force
Copy-Item $newApk (Join-Path $folderCaiDat "ChatAi_Moi_Nhat.apk") -Force
Copy-Item $newApk (Join-Path $desktop "ChatAi_Moi_Nhat.apk") -Force -ErrorAction SilentlyContinue

Write-Host "Dong goi ma nguon Android Kotlin ChatAiAdr_Full.zip..." -ForegroundColor Cyan
$zipAdr = Join-Path $workspace "ChatAiAdr_Full.zip"
if (Test-Path $zipAdr) { Remove-Item -Force $zipAdr }

# Nen app, gradle, build.gradle, settings.gradle, keystore (khong nen thu muc build tam)
$exclude = @("build", ".gradle", ".idea", "*.apk", "*.zip")
Compress-Archive -Path "$workspace\app", "$workspace\gradle", "$workspace\keystore", "$workspace\build.gradle", "$workspace\settings.gradle", "$workspace\gradlew", "$workspace\gradlew.bat", "$workspace\keystore.properties", "$workspace\local.properties" -DestinationPath $zipAdr -Force

Copy-Item $zipAdr (Join-Path $downloads "ChatAiAdr_Full.zip") -Force
Copy-Item $zipAdr (Join-Path $folderCaiDat "ChatAiAdr_Full.zip") -Force
Copy-Item $zipAdr (Join-Path $desktop "ChatAiAdr_Full.zip") -Force -ErrorAction SilentlyContinue

Write-Host "`n=== CAP NHAT HOAN TAT CAC FILE NGUON VA CAI DAT ===" -ForegroundColor Green
Get-ChildItem $folderCaiDat | Select-Object Name, Length, LastWriteTime | Format-Table -AutoSize
