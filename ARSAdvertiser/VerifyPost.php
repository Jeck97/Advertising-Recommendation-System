<?php
require('AdminNav.php');
include('includes/dbconfig.php');
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
			border-bottom: 10px solid #ddd;
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
		<center><h1>VERIFY PENDING POST</h1></center>
		<br><center><input type="text" id="myInput" onkeyup="myFunction()" placeholder="Search for post title only" title="Type in post title"></center><br>
		<table id="myTable">
			<tr>
				<th><center>TITLE</center></th>
				<th><center>STATUS</center></th>
				<th><center>POST DETAILS</center></th>
				<th><center>ACTION</center></th>	
			</tr>

<?php
                $reference = "advertisement/";
                $fetchdata = $database->getReference($reference)->getValue();
                foreach($fetchdata as $key => $row)
                {



                    if ($row['status'] == "advertisement_status_pending") {
                      // code...

?>
                            <form method="get" action="Action.php">
                        <tr>
                            <input type="hidden" name="postid" value="<?php echo $key; ?>">

                            <td><input type="hidden" name="title" value="<?php echo $row['title'];?>"><center><?php echo $row['title']; ?></center></td>

                            <td><input type="hidden" name="status" value="<?php echo $row['status'];?>"><center><?php echo $row['status']; ?></center></td>

                            <td><input type="hidden" name="description" value="<?php echo $row['description'];?>"><center><?php echo $row['description']; ?></center></td>

                            <td>
                            <center>
                                    <input type="hidden" name="token" value="<?php echo $key ?>">
                                    <input type="submit" name="verify" value="VERIFY" class="button">
                                    </form>
                            </td>

                        </tr>

<?php

                              }
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