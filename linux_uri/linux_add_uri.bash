sudo cp linux_uri/starfish.desktop ~/.local/share/applications
echo [Default Applications] >> ~/.local/share/applications/mimeapps.list 
echo x-scheme-handler/ide=starfish.desktop >> ~/.local/share/applications/mimeapps.list
echo "I AM RUNNING"
sudo update-desktop-database
