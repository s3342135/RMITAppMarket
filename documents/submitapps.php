<?php

/**
 * Submit new app to market.
 * Receive data from GET.
 * Receive file from FILES.
 */

$input_appName = $_GET['appname'];
$input_appDesc = $_GET['desc'];
$input_appStar;
$input_appPath;
$input_appUpdateAvail;

// Login to database
include("dbConnect.php");

// Try get appName from database.
// Used to check new/updated app.
$sql = "SELECT * FROM android_AppList WHERE appName='$input_appName'";
mysql_query($sql);
$array = mysql_fetch_array($sql);

// Check new or updated app.
if ($array == null) {
	//new app, do what?
	$input_appUpdateAvail = "F";
	$input_appStar = 0;
} else {
	//old one! update!
	$input_appUpdateAvail = "T";
	$id = $array['id'];
}

// Get app file
$filename=$_FILES['upload']['name'];
$filesize=$_FILES['upload']['size'];
$tmp_name=$_FILES['upload']['tmp_name'];
$uploads_dir = "uploads";
$finalfilename=$id."_".$input_appName;

$result=move_uploaded_file($tmp_name,"$uploads_dir/$finalfilename");

// Set appPath to the .apk file uploaded.
$input_appPath = "$uploads_dir/$finalfilename";

// If upload succeeded, add info to database. Else, return error.
if ($result) {
	$importfile="INSERT INTO android_AppList(appName,appDesc,appStar,appPath,updateAvail) VALUES('$input_appName','$input_appDesc','$input_appStar','$input_appPath','$input_appUpdateAvail')";
	mysql_query($importfile) or die("1");
	echo "0";
} else {
	echo "1";
}
?>