# Script ho tro tai ung dung len Appetize.io
param (
    [string]$AppetizeApiToken = ""
)

$ApkFile = ".\ChatAiAdr-debug.apk"
if (-not (Test-Path $ApkFile)) {
    $ApkFile = ".\app\build\outputs\apk\debug\app-debug.apk"
}

if (-not (Test-Path $ApkFile)) {
    Write-Host "Khong tim thay file APK! Vui long chay .\scripts\build_apk.ps1 truoc." -ForegroundColor Red
    exit 1
}

$apkItem = Get-Item $ApkFile
Write-Host "File APK san sang: $($apkItem.FullName) ($([math]::Round($apkItem.Length / 1MB, 2)) MB)" -ForegroundColor Cyan

if ($AppetizeApiToken -ne "") {
    Write-Host "Dang upload APK len Appetize.io bang API Token..." -ForegroundColor Yellow
    
    $headers = @{
        "Authorization" = "Bearer $AppetizeApiToken"
    }
    
    # Upload via curl
    $curlOutput = curl.exe -s -X POST "https://api.appetize.io/v2/apps" `
        -H "Authorization: Bearer $AppetizeApiToken" `
        -F "file=@$($apkItem.FullName)" `
        -F "platform=android"
        
    Write-Host "Ket qua tu Appetize.io API:" -ForegroundColor Green
    Write-Host $curlOutput
} else {
    Write-Host "`n=== HUONG DAN CHAY TREN MAY AO APPETIZE.IO ===" -ForegroundColor Green
    Write-Host "1. Mo trinh duyet truy cap: https://appetize.io/upload" -ForegroundColor White
    Write-Host "2. Keo va tha file APK vao trang web:" -ForegroundColor White
    Write-Host "   $($apkItem.FullName)" -ForegroundColor Cyan
    Write-Host "3. Chon thiet bi (vi du: Pixel 7, Android 13) va bam Play!" -ForegroundColor White
    Write-Host "--------------------------------------------------"
    Write-Host "Dang mo trang appetize.io/upload tren trinh duyet..." -ForegroundColor Yellow
    Start-Process "https://appetize.io/upload"
}
