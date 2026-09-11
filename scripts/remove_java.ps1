$srcDir = "app\src\main\java"
$javaFiles = Get-ChildItem -Path $srcDir -Recurse -Filter "*.java"
Write-Host "Tim thay $($javaFiles.Count) file Java cu, tien hanh xoa bo..." -ForegroundColor Yellow

foreach ($file in $javaFiles) {
    Remove-Item -Path $file.FullName -Force
    Write-Host "  - Da xoa: $($file.Name)" -ForegroundColor DarkGray
}

$ktFiles = Get-ChildItem -Path $srcDir -Recurse -Filter "*.kt"
Write-Host "`nTong so file Kotlin thuan 100% hien tai: $($ktFiles.Count)" -ForegroundColor Green
foreach ($file in $ktFiles) {
    Write-Host "  + Kotlin: $($file.Name)" -ForegroundColor Cyan
}

$remainingJava = Get-ChildItem -Path $srcDir -Recurse -Filter "*.java"
Write-Host "So file Java con lai: $($remainingJava.Count) (Phai bang 0)" -ForegroundColor Green
