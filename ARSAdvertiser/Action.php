<?php

require('AdminNav.php');
include('includes/dbconfig.php'); 

	session_start();

	if (isset($_POST['submit'])){
		$status = $_POST['status'];
		$token = $_POST['ref_token'];

		$data =[
			'status' => $status,
		];

		$ref = "advertisement/".$token;
		$postdata = $database->getReference($ref)->update($data);

		if ($postdata){
			echo "<script>alert('Successfully updated!');</script>";
			echo "<script>location.assign('VerifyPost.php');</script>";
		}
		else{
			echo "<script>alert('Failed to update!');</script>";
			echo "<script>location.assign('VerifyPost.php');</script>";
		}
	}
?>
<!DOCTYPE html>
<html>
<head>
	<title></title>
	<style>
		.bg-img{
			background:url(image/backgrounds.jpg);
			background-size: cover;
			background-repeat: no-repeat;
		}
		.bg-sm{
			background-color: rgba(255,255,255,0.8);
			width:1000px;
			height:100%;
			border-radius: 25px;
			padding: 10px 10px 30px 10px;
			margin-top: 10px;
			margin-left: 200px;
			display: inline-table;
		}
		.button {
			background-color: rgba(3, 227, 252,0.5);
			border-color: black;
			color: black;
			text-align: center;
			text-decoration: none;
			display: inline-block;
			padding: 10px;
			font-size: 16px;
			width: 150px;
		}
		.button:hover {
			background-color: rgba(3, 227, 252,0.8);
		}
		table {
			border-collapse: collapse;
			width: 100%;
		}
		th {
			text-align: left;
			padding: 20px;
			width:150px;
		}
		td {
			text-align: left;
			padding: 8px;
		}
		tr:nth-child(even) {background-color: #f2f2f2;}
		tr:nth-child(odd) {background-color: #f2f2f2;}

		.dropbtn {
  		background-color: rgba(3, 227, 252,0.5);
  		border-color: black;
  		color: black;
  		padding: 8px;
  		font-size: 14px;
  		cursor: pointer;
		}

		.dropdown {
 		 position: relative;
 		 display: inline-block;
		}

		.dropdown-content {
 		display: none;
  		position: absolute;
  		background-color: rgba(3, 227, 252,0.5);
  		min-width: 160px;
  		box-shadow: 0px 8px 16px 0px rgba(0,0,0,0.2);
  		z-index: 1;
		}

		.dropdown-content a {
  		color: black;
  		padding: 12px 16px;
  		text-decoration: none;
  		display: block;
		}

		.dropdown-content a:hover {background-color: rgba(3, 227, 252,0.5)}

		.dropdown:hover .dropdown-content {
  		display: block;
  		border-color: black;
		}

		.dropdown:hover .dropbtn {
  		background-color: rgba(3, 227, 252,0.5);
  		border-color: black;
		}
	</style>
</head>
<body class="bg-img">
		<div class="bg-sm">
			<form method="post">
			<center><h1> VERIFY PENDING POST</h1></center>
			<br>

<?php
			if (isset($_GET['token'])){
			$token = $_GET['token'];
			$ref = "advertisement/";
			$row1 = $database->getReference($ref)->getChild($token)->getValue();

			$title = $row1['title'];
			$description = $row1['description'];
			}
?>

			<table>
				<input type="hidden" name="ref_token" value="<?php echo $token; ?>">
				<tr>
					<th>Title</th>
					<td><input type="hidden" name="title" value="<?php echo ['title'];?>"><center><?php echo  $row1['title'];?></center></td>
				</tr>
				<tr>
					<th>Post Details</th>
					<td><input type="hidden" name="description" value="<?php echo ['description'];?>"><center><?php echo  $row1['description'];?></center></td>
				</tr>

				<tr>
					<th>Status</th>
					<td><select class="dropbtn" name="status">
						<div class="dropdown-content">
						<center>
						<option>--Pending--</option>
						<option value="advertisement_status_activated">Accept</option>
						<option value="advertisement_status_rejected">Reject</option></select></td></center>
						
				</tr>

			</table>
			<br>
			<center><input style="width: 200px;" type="submit" name="submit" value=" SUBMIT " class="button"></center>
		</div>
	</form>

<!-- The core Firebase JS SDK is always required and must be listed first -->
<script src="https://www.gstatic.com/firebasejs/8.2.0/firebase-app.js"></script>

<!-- TODO: Add SDKs for Firebase products that you want to use
     https://firebase.google.com/docs/web/setup#available-libraries -->
<script src="https://www.gstatic.com/firebasejs/8.2.0/firebase-analytics.js"></script>

<script>
  // Your web app's Firebase configuration
  // For Firebase JS SDK v7.20.0 and later, measurementId is optional
  var firebaseConfig = {
    apiKey: "AIzaSyAtw37bMWZyZii9fMw10Pkr73FAs4hkAVA",
    authDomain: "advertising-recommend-sy-85005.firebaseapp.com",
    databaseURL: "https://advertising-recommend-sy-85005.firebaseio.com",
    projectId: "advertising-recommend-sy-85005",
    storageBucket: "advertising-recommend-sy-85005.appspot.com",
    messagingSenderId: "857850429499",
    appId: "1:857850429499:web:95f790c067da6b8c3c1c1e",
    measurementId: "G-R67QWB040W"
  };
  // Initialize Firebase
  firebase.initializeApp(firebaseConfig);
  firebase.analytics();
</script>	
</body>
</html>