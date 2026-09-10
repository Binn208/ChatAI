# Script kiem tra va dong goi du an ChatAiIos (iOS Native SwiftUI)
param (
    [string]$WorkspaceDir = (Split-Path -Parent $PSScriptRoot),
    [string]$DownloadDir = [System.IO.Path]::Combine($env:USERPROFILE, "Downloads")
)

Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "   KIEM TRA VA DONG GOI BAN IOS NATIVE    " -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan

$IosDir = Join-Path $WorkspaceDir "ios"
if (-not (Test-Path $IosDir)) {
    Write-Error "Khong tim thay thu muc ios tai: $IosDir"
    exit 1
}

# 1. Kiem tra danh sach file Swift
$swiftFiles = Get-ChildItem -Path $IosDir -Recurse -Filter "*.swift"
Write-Host "Tim thay $($swiftFiles.Count) file ma nguon Swift:" -ForegroundColor Green
foreach ($file in $swiftFiles) {
    Write-Host "  - $($file.Name)" -ForegroundColor DarkGray
}

# 2. Dong goi ZIP tai workspace
$ZipWorkspace = Join-Path $WorkspaceDir "ChatAiIos_Full.zip"
if (Test-Path $ZipWorkspace) { Remove-Item -Force $ZipWorkspace }

Write-Host "Dang nen thu muc ios thanh: $ZipWorkspace ..." -ForegroundColor Yellow
Compress-Archive -Path "$IosDir\*" -DestinationPath $ZipWorkspace -Force

# 3. Sao chep vao Downloads
$ZipDownload = Join-Path $DownloadDir "ChatAiIos_Full.zip"
Copy-Item -Path $ZipWorkspace -Destination $ZipDownload -Force
Write-Host "Da xuat file ZIP ra thu muc Downloads: $ZipDownload" -ForegroundColor Green

# 4. Sao chep vao 00_FILE_CAI_DAT_CHO_BAN
$FolderCaiDat = Join-Path $WorkspaceDir "00_FILE_CAI_DAT_CHO_BAN"
if (-not (Test-Path $FolderCaiDat)) { New-Item -ItemType Directory -Path $FolderCaiDat -Force | Out-Null }
$ZipCaiDat = Join-Path $FolderCaiDat "ChatAiIos_Full.zip"
Copy-Item -Path $ZipWorkspace -Destination $ZipCaiDat -Force
Write-Host "Da sao chep vao: $ZipCaiDat" -ForegroundColor Green

Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "   HOAN TAT DONG GOI DU AN IOS THANH CONG  " -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan
