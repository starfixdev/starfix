# StarfishDemo

---

## Installing

Compile files with `javac CreateProject.java OpenFile.java`
and run `sudo java CreateProject` to setup URI handler in windows and linux

## Usage

### 1. To clone and open any repository in VSCode `ide://clone-url?` in front of link

Example to clone
>`https://github.com/DhyanCoder/starfish`

change it to
>`ide://clone-url?https://github.com/DhyanCoder/starfish`

### 2. To Open any folder

- In Windows change
    >`C:\Path\to\folder`

    to
    >`ide://open-file?C:path\to\folder`

- In Linux to open path in VSCode
    >`xdg-open ide://open-file?path\to\folder`
