@echo off
set "ANDROID_SDK_ROOT=C:\Users\Bemo\AppData\Local\Android\Sdk"
set "ANDROID_HOME=C:\Users\Bemo\AppData\Local\Android\Sdk"
echo Khoi dong Android Emulator Pixel_8 voi SDK chuan tai: %ANDROID_SDK_ROOT%
"C:\Users\Bemo\AppData\Local\Android\Sdk\emulator\emulator.exe" -avd Pixel_8
