$workspace = (Split-Path -Parent $PSScriptRoot)
$downloads = [System.IO.Path]::Combine($env:USERPROFILE, "Downloads")
$desktop = [System.Environment]::GetFolderPath("Desktop")
$folderCaiDat = Join-Path $workspace "00_FILE_CAI_DAT_CHO_BAN"

if (-not (Test-Path $folderCaiDat)) {
    New-Item -ItemType Directory -Path $folderCaiDat -Force | Out-Null
}

Write-Host "Dang dong goi file zip iOS moi nhat..." -ForegroundColor Cyan
$iosDir = Join-Path $workspace "ios"
$zipIos = Join-Path $workspace "ChatAiIos_Full.zip"
if (Test-Path $zipIos) { Remove-Item -Force $zipIos }
Compress-Archive -Path "$iosDir\*" -DestinationPath $zipIos -Force

$zipAdr = Join-Path $workspace "ChatAiAdr_Full.zip"
$apkAdr = Join-Path $workspace "ChatAi_Moi_Nhat.apk"

# Sao chep vao Downloads
Write-Host "Sao chep vao Downloads..." -ForegroundColor Yellow
Copy-Item $zipIos (Join-Path $downloads "ChatAiIos_Full.zip") -Force
Copy-Item $zipAdr (Join-Path $downloads "ChatAiAdr_Full.zip") -Force
Copy-Item $apkAdr (Join-Path $downloads "ChatAi_Moi_Nhat.apk") -Force

# Sao chep vao 00_FILE_CAI_DAT_CHO_BAN
Write-Host "Sao chep vao 00_FILE_CAI_DAT_CHO_BAN..." -ForegroundColor Yellow
Copy-Item $zipIos (Join-Path $folderCaiDat "ChatAiIos_Full.zip") -Force
Copy-Item $zipAdr (Join-Path $folderCaiDat "ChatAiAdr_Full.zip") -Force
Copy-Item $apkAdr (Join-Path $folderCaiDat "ChatAi_Moi_Nhat.apk") -Force

# Sao chep ra Desktop
Write-Host "Sao chep ra Desktop..." -ForegroundColor Yellow
Copy-Item $zipIos (Join-Path $desktop "ChatAiIos_Full.zip") -Force -ErrorAction SilentlyContinue
Copy-Item $zipAdr (Join-Path $desktop "ChatAiAdr_Full.zip") -Force -ErrorAction SilentlyContinue
Copy-Item $apkAdr (Join-Path $desktop "ChatAi_Moi_Nhat.apk") -Force -ErrorAction SilentlyContinue

Write-Host "`n=== DANH SACH CAC FILE SAN SANG CHO BAN ===" -ForegroundColor Green
Get-ChildItem $folderCaiDat | Select-Object Name, Length, LastWriteTime | Format-Table -AutoSize
