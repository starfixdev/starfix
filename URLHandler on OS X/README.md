SublimeText 2/3 URLHandler on OS X
=======================
This application enables SublimeText 2/3 to open URL scheme  `subl:`  or `txmt`

	`subl://open/?url=file:///etc/passwd&line=10&column=2`
	or
	`txmt://open/?url=file:///etc/passwd&line=10&column=2`

Installation
=============
1. First download [SublHandler from here](http://asuth.com/SublHandler.app.zip).

2. Unzip it.

3. Launch it. 

4. Select `SublHandler` -> `Preferences...`, then set the path for the subl binary.


Testing
=========
Open terminal and type:
   ` open 'subl://open/?url=file:///etc/hosts'`


Uninstall
===============
Delete following:

    /Applications/SublHandler.app
    ~/Library/Preferences/com.asuth.sublhandler.plist


Reference
==========
[Textmate](http://manual.macromates.com/en/using_textmate_from_terminal#url_scheme_html)


