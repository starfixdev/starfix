@echo off
cd %~dp0
xcopy /s/i "%~dp0\Starfix" "C:\Users\Public\Starfix"
echo Please accept the upcoming Prompts to install successfully
"%~dp0\starfix.reg" 
setx path "%path%;C:\Users\Public\Starfix"
PAUSE