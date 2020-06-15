@echo off
cd %~dp0
xcopy /s/i "%~dp0\Starfish" "C:\Users\Public\Starfish"
echo Please accept the upcoming Prompts to install successfully
"%~dp0\starfish.reg" 
setx path "%path%;C:\Users\Public\Starfish"
PAUSE