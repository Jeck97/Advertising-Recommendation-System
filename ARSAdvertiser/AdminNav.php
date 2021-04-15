<!DOCTYPE html>
<html>
<head>
	<title>Header</title>
	<style>
		.bg{
			background-color: #03e3fc;
			padding: 15px;
		}
		.bg-side{
			background-color: #04b3c7;
			padding: 20px;
			width: 150px;
			height: 250%;
			position:absolute;
		}
		.text{
			text-decoration:none;
			color: black;
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
			cursor: pointer;
		}
	</style>
</head>
<body>
	<a href="AdminHome.php" class="text">
	<div class="bg">
	<center><h1>Advertising Recommendation System</h1></center>
	</div>
	</a>
	<center>
	<div class="bg-side">
		<div><a href="ViewPlan.php"><button class="button">Plan Management</button></a></div>
		<br>
		<div><a href="VerifyPost.php"><button class="button">Verify Post</button></a></div>
		<br>
		<div><a href="ViewPost.php"><button class="button">View Post</button></a></div>
		<br>
	</div>
	</center>
</body>
</html>