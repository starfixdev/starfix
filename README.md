# StarfishDemo

---

## Installing

Compile files with `javac URIData.java OS.java StarfishMain.java`
and run `install_win.bat` to setup URI handler in windows and `bash install_linux.bash` linux

## Usage

### 1. To clone and open any repository in VSCode `ide://clone-url?url=` in front of link

Example to clone
>`https://github.com/DhyanCoder/starfish`

change it to
>`ide://clone-url?ide=vscode&path=path/to/folder&url=https://github.com/DhyanCoder/starfish`

### 2. To Open any folder

- In Windows change
    >`C:\Path\to\folder`

    to
    >`ide://open-file?ide=vscode&path=C:path\to\folder`

    or run command
    >`start ide://open-file?ide=vscode&path=C:path\to\folder`

- In Linux to open path in VSCode
    >`xdg-open ide://open-file?ide=vscode&path=C:path\to\folder`
