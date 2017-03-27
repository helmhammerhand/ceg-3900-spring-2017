
The android project files are contianed in the Easy_xkcd directory.
The cloud server files (including password lists) are contained in the web server directory.

The web server is currently running at 
http://ec2-35-166-54-53.us-west-2.compute.amazonaws.com/index.php
The web server get parameters and their usages are outlined in the p5 submission pdf and
in the web server/index.php file.

The folowing changes were made to the Easy_XKCD APK as outlined in report-p5.pdf
- Added menu items to menu_comic_fragment.xml
	- Add Tag: create popup that takes a text input for the tag and send request to server
	- Remove Tag: popup takes input for the tag and asks server to remove it from the current comic
	- Search Tag: make user enter tag and then list all comics with the tag
	- View Tags: list all tags associated with the current comic
	- List Tags: displays all the tags that have been created for all comics
- Added listener code for the new menu items in ComicFragment.onOptionsItemSelected(). Functions that require user input such as add, remove, or search tag use a popup menu with a text box to get a string from the user.
- Added an IntentService class TagQueryService which communicates to the EC2 server. Updated manifest file with this service
- Added a broadcast receiver in ComicFragment.onCreate() to listen for callbacks from TagQueryService. The receiver displays the server’s response on screen using the already existing showTranscript method which creates a popup box with some text.

