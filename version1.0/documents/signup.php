<?php

/**
 * Signup for new account.
 * Receive data from parameter.
 */

$input_fullname = $_GET['fullname'];
$input_id = $_GET['id'];
$input_passwd = $_GET['passwd'];

// Login to database
include("dbConnect.php");

// Check ID existance
$id = "SELECT * FROM android_User WHERE id='$input_id'";
$query = mysql_query($id);
$result_id = mysql_fetch_array($query);
if ($result_id == null) {
	$sql = "INSERT INTO android_User VALUES('$input_id', '$input_passwd', '$input_fullname')";
	mysql_query($sql) or die("1");
	echo "0";
} else {
	echo "1";
}
?>