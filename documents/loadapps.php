<?php

/**
 * Load all app list from server.
 */

// Login to database
include("dbConnect.php");

//
$sql = "SELECT * FROM android_AppList";
$query=mysql_query($sql);
while($array = mysql_fetch_array($query)) {
	$appName = $array['appName'];
	$appDesc = $array['appDesc'];
	$appAvatar = $array['appAvatar'];
	$appStar = $array['appStar'];
	$appPath = $array['appPath'];
	$updateAvail = $array['updateAvail'];
	
	// output
	// pattern: Teris--a game--/imgs/test.png--0--/uploads/file.apk--false##Tank--a test--/imgs/test.png--5--/uploads/file2.apk--true##...
	echo $appName;
	echo "--";
	echo $appDesc;
	echo "--";
	echo $appAvatar;
	echo "--";
	echo $appStar;
	echo "--";
	echo $appPath;
	echo "--";
	echo $updateAvail;
	echo "##";
}

?>