## This script builds osx app based on the applescript file.
## 
rm -rf starfix.app
osacompile -o starfix.app starfix-urlhandler.applescript
plutil -insert CFBundleIdentifier -string dev.starfix starfix.app/Contents/Info.plist
plutil -insert CFBundleURLTypes -xml '
        <array>
          <dict>
            <key>CFBundleTypeRole</key>
            <string>Viewer</string>
            <key>CFBundleURLName</key>
            <string>com.apple.ScriptEditor.CustomURLScheme</string>
            <key>CFBundleURLSchemes</key>
            <array>
              <string>ide</string>
            </array>
          </dict>
        </array>
        ' starfix.app/Contents/Info.plist
cp starfix  starfix.app/Contents/Resources/starfix

