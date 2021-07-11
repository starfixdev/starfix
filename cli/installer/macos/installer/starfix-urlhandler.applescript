on open location this_URL
	set AppleScript's text item delimiters to ":"
	set uri_elements to every text item of this_URL
	set protocol to item 1 of uri_elements
	set the script_file to POSIX path of (path to resource "starfix")

	set the scriptcmd to script_file & " '" & this_URL & "' 2>&1"

(* display dialog purely for debug as shell script output is lost *)
	## display dialog "Wonka " & scriptcmd

	do shell script scriptcmd
end open location

