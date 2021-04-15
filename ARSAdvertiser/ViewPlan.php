<?php

require('AdminNav.php');
include('includes/dbconfig.php');

	if (isset($_POST['delete']))
	{
		$token = $_POST['ref_token_delete'];

		$ref = "plan/".$token;
		$deleteData = $database->getReference($ref)->remove();

		if ($deleteData){
			echo "<script>alert('Successfully deleted!');</script>";
			echo "<script>location.assign('ViewPlan.php');</script>";
		}else{
			echo "<script>alert('Failed to delete!');</script>";
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
			width:1050px;
			height:100%;
			border-radius: 25px;
			padding: 10px 10px 30px 10px;
			margin-top: 10px;
			margin-left: 250px;
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
		.btn btn-primary {
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
		.button-sm {
			padding: 0px;
			font-size: 14px;
			width: 80px;
		}
		.button:hover {
			background-color: rgba(3, 227, 252,0.8);
		}
		table {
			border-collapse: collapse;
			width: 100%;
		}
		th, td {
			padding: 8px;
			text-align: left;
			border-bottom: 1px solid #ddd;
		}
		tr:hover {background-color:#f5f5f5;}

		#myInput {
			background-image: url('https://www.w3schools.com/css/searchicon.png');
		 	background-position: 10px 10px;
			background-repeat: no-repeat;
			width: 50%;
			font-size: 16px;
			font-style: italic;
			padding: 12px 20px 12px 40px;
			border: 1px solid #ddd;
			margin-bottom: 12px;
		}

		#myTable {
		  border-collapse: collapse;
		  width: 100%;
		  border: 1px solid #ddd;
		  font-size: 18px;
		}

		#myTable th, #myTable td {
		  text-align: left;
		  padding: 12px;
		}

		#myTable tr {
		  border-bottom: 1px solid #ddd;
		}

		#myTable tr.header, #myTable tr:hover {
		  background-color: #f1f1f1;
		}
	</style>
</head>
<body class="bg-img">
	<div class="bg-sm">
		<center><h1>PLAN MANAGEMENT</h1></center>
		<br><center><input type="text" id="myInput" onkeyup="myFunction()" placeholder="Search for plan name only" title="Type in plan name"></center>
		<div><center><a href="AddPlan.php"><button class="button">ADD PLAN</button></a></center></div>
		<br>
		<table id="myTable">
			<tr>
				<th><center>PLAN NAME</center></th>
				<th><center>DETAILS</center></th>
				<th><center>PRICE</center></th>
				<th><center>ACTION</center></th>
			</tr>
			<?php
				$ref = "plan";
				$fetchdata = $database->getReference($ref)->getValue();
				$i = 0;	
				if ($fetchdata >0)
				{			
				foreach($fetchdata as $key => $row)
				{

					$i++;
			?>
			<form method="post">
						<input type="hidden" name="token" value="<?php echo $i; ?>">	
						<tr>
							<td>
							<input type="hidden" name="name" value="<?php echo $row['name'];?>"><center><?php echo $row['name']; ?></center></td>
							<td>
							<input type="hidden" name="description" value="<?php echo $row['description']; ?>"><center><?php echo $row['description']; ?></center>
							</td>
							<td>
							<input type="hidden" name="price" value="<?php echo $row['price']; ?>"><center>RM <?php echo $row['price']; ?></center>
							</td>
							<td>
							<center><button><a href="ModifyPlan.php?token=<?php echo $key; ?>"class="button">MODIFY</a></button>
							</center>
							<center>
							<input type="hidden" name="ref_token_delete" value="<?php echo $key ?>">
								<button type="submit" name="delete" class="button">DELETE</button>
							</center>
							</td>
						</tr>
						<?php

						}
							}
						else
						{
						?>
						<tr class="text-center">
							<td colspan = "8">Data Not Available in Firebase(Database)</td>
						</tr>
						<?php
						}
						?>
					</form>
		</table>
	</div>

	<script>
			function myFunction() {
			  var input, filter, table, tr, td, i, txtValue;
			  input = document.getElementById("myInput");
			  filter = input.value.toUpperCase();
			  table = document.getElementById("myTable");
			  tr = table.getElementsByTagName("tr");
			  for (i = 0; i < tr.length; i++) {
			    td = tr[i].getElementsByTagName("td")[0];
			    if (td) {
			      txtValue = td.textContent || td.innerText;
			      if (txtValue.toUpperCase().indexOf(filter) > -1) {
			        tr[i].style.display = "";
			      } else {
			        tr[i].style.display = "none";
			      }
			    }       
			  }
			}
</script>

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