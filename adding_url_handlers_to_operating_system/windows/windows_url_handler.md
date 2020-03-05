# Adding Custom Url Handlers in Windows

##### To register an application to handle a particular URI scheme, add a new key, along with the appropriate subkeys and values, to HKEY_CLASSES_ROOT.
##### The root key must match the URI scheme that is being added. For instance, to add an "ide:" scheme, add an ide key to HKEY_CLASSES_ROOT, as follows:

      HKEY_CLASSES_ROOT
         alert
             URL Protocol = "" 

- Under this new key, the URL Protocol string value indicates that this key declares a custom pluggable protocol handler. Without this key, the handler application will not launch. The value should be an empty string.

- Keys should also be added for DefaultIcon and shell. The Default string value of the DefaultIcon key must be the file name to use as an icon for this new URI scheme. The string takes the form "path, iconindex" with a maximum length of MAX_PATH. The name of the first key under the shell key should be an action verb, such as open. Under this key, a command key or a DDEEXEC key indicate how the handler should be invoked. The values under the command and DDEEXEC keys describe how to launch the application handling the new protocol.
- Finally, the Default string value should contain the display name of the new URI scheme. 

### The following code shows how to register an application, ide.exe in this case, to handle the ide scheme:
     HKEY_CLASSES_ROOT
        alert
           (Default) = "URL:Ide Protocol"
           URL Protocol = ""
           DefaultIcon
              (Default) = "ide.exe,1"
           shell
              open
                 command
                    (Default) = "C:\Program Files\Ide\ide.exe" "%1"

 
