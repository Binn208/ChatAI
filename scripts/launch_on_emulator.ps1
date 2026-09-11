$adb = "C:\Users\Bemo\AppData\Local\Android\Sdk\platform-tools\adb.exe"
$apk = Join-Path $PSScriptRoot "..\ChatAiAdr-release.apk"

Write-Host "Dang cho Emulator khoi dong xong..." -ForegroundColor Yellow
& $adb wait-for-device

# Cho sys.boot_completed
$booted = $false
for ($i = 0; $i -lt 30; $i++) {
    $res = & $adb shell getprop sys.boot_completed 2>$null
    if ($res -and $res.Trim() -eq "1") {
        $booted = $true
        break
    }
    Start-Sleep -Seconds 2
}

Write-Host "Emulator da san sang! Dang cai dat APK: $apk ..." -ForegroundColor Cyan
& $adb install -r $apk

Write-Host "Dang khoi chay ung dung tren dien thoai ao..." -ForegroundColor Green
& $adb shell am start -n com.chatai.adr/.MainActivity
Write-Host "Ung dung da duoc mo tren may ao!" -ForegroundColor Green
