<?php

/**
 * Signup for new account.
 * Receive data from parameter.
 */

$id = $_GET['id'];
$passwd = $_GET['passwd'];

// Login to database
include("dbConnect.php");

// Make sure to validate data before using this method.
// Login
$sql="SELECT * FROM android_User WHERE id='$id' AND passwd='$passwd'";
$result = mysql_query($sql);

// Login Success: return 0
// Login Failed: return 1
$login = mysql_fetch_array($result);
if ($login == null) {
	echo "1";
} else {
	echo "0";
}
?>