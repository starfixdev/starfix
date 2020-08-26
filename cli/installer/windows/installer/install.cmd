@echo off
cd %~dp0
xcopy /s/i "%~dp0\starfix" "C:\Users\Public\starfix"
echo Please accept the upcoming Prompts to install successfully
"%~dp0\starfix.reg" 
setx path "%path%;C:\Users\Public\starfix"
PAUSE