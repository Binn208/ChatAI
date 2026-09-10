param(
    [string]$OutputFile = "emulator_screen.png",
    [int]$TapX = 0,
    [int]$TapY = 0,
    [int]$DelaySec = 1
)

$adb = "C:\Users\Bemo\AppData\Local\Android\Sdk\platform-tools\adb.exe"

if ($TapX -gt 0 -and $TapY -gt 0) {
    & $adb shell input tap $TapX $TapY
    Start-Sleep -Seconds $DelaySec
}

$destination = Join-Path $PSScriptRoot "..\$OutputFile"
$destination = [System.IO.Path]::GetFullPath($destination)

cmd.exe /c "`"$adb`" exec-out screencap -p > `"$destination`""

if (Test-Path $destination) {
    Write-Host "Captured $OutputFile successfully, size: $((Get-Item $destination).Length) bytes"
} else {
    Write-Host "Failed to capture $OutputFile"
}
