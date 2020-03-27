@echo off
set str=%~dp0
echo %str%
set str=%str:\=\\%
set str2=@="\"%str%Starfish.bat\" \"%%1\""
echo %str2%
echo %str2% >> windows_uri/wuri_ide.reg
reg import windows_uri/wuri_ide.reg