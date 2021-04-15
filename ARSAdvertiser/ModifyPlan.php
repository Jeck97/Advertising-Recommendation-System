<?php

require('AdminNav.php');
include('includes/dbconfig.php'); 

	session_start();

	if (isset($_POST['modifyplan'])){
		$name = $_POST['name'];
		$durationDay = $_POST['durationDay'];
		$durationMonth = $_POST['durationMonth'];
		$durationYear = $_POST['durationYear'];
		$description = $_POST['description'];
		$price = $_POST['price'];
		$token = $_POST['ref_token'];

		$data =[
			'name' => $name,
			'durationDay' => $durationDay,
			'durationMonth' => $durationMonth,
			'durationYear' => $durationYear,
			'description' => $description,
			'price' => $price,
		];

		$ref = "plan/".$token;
		$postdata = $database->getReference($ref)->update($data);

		if ($postdata){
			echo "<script>alert('Successfully updated!');</script>";
			echo "<script>location.assign('ViewPlan.php');</script>";
		}
		else{
			echo "<script>alert('Failed to update!');</script>";
			echo "<script>location.assign('ViewPlan.php');</script>";
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
	</style>
</head>
<body class="bg-img">
		<div class="bg-sm">
			<form method="post">
			<center><h1> MODIFY PLAN</h1></center>
			<br>

<?php
			if (isset($_GET['token'])){
			$token = $_GET['token'];
			$ref = "plan/";
			$row1 = $database->getReference($ref)->getChild($token)->getValue();

			$name = $row1['name'];
			$durationDay = $row1['durationDay'];
			$durationMonth = $row1['durationMonth'];
			$durationYear = $row1['durationYear'];
			$description = $row1['description'];
			$price = $row1['price'];
		}
?>
			<table>
				<input type="hidden" name="ref_token" value="<?php echo $token; ?>">
				<tr>
					<th>Plan Name</th>
					<td><input type="text" name="name" value="<?php echo $name; ?>" required>
				</tr>
				<tr>
					<th>Description</th>
					<td><input type="text" name="description" value="<?php echo $description; ?>" required></td>
				</tr>
				<tr>
					<th>Duration (Day)</th>
					<td><input type="number" name="durationDay" min="0" max="31" value="<?php echo $durationDay; ?>" required></td>
				</tr>
				<tr>
					<th>Duration (Month)</th>
					<td><input type="number" name="durationMonth" min="0" max="12" value="<?php echo $durationMonth; ?>" required></td>
				</tr>
				<tr>
					<th>Duration (Year)</th>
					<td><input type="number" name="durationYear" min="0" value="<?php echo $durationYear; ?>" required></td>
				</tr>
				<tr>
					<th>Price(RM)</th>
					<td><input type="number" name="price"  step="any" min="0" value="<?php echo $price; ?>" required></td>
				</tr>
			</table>
			<br>
			<center><input style="width: 200px;" type="submit" name="modifyplan" value=" MODIFY " class="button"></center>
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