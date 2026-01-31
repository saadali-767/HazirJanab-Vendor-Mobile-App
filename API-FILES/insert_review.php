<?php
$host = 'localhost'; // or your host
$dbname = 'hazirjanab'; // your database name
$username = 'root'; // your database username
$password = ''; // your database password

// Create connection
$conn = new mysqli($host, $username, $password, $dbname);

// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

$user_id = isset($_POST['user_id']) ? $_POST['user_id'] : '';
$vendor_id = isset($_POST['vendor_id']) ? $_POST['vendor_id'] : '';
$description = isset($_POST['description']) ? $_POST['description'] : '';

// Prepare the SQL statement
$stmt = $conn->prepare("INSERT INTO review (user_id, vendor_id, description) VALUES (?, ?, ?)");
$stmt->bind_param("iis", $user_id, $vendor_id, $description);

// Execute the statement and check for errors
if ($stmt->execute()) {
    echo json_encode(array("status" => "success", "message" => "Review inserted successfully"));
} else {
    echo json_encode(array("status" => "error", "message" => "Error inserting review"));
}

$stmt->close();
$conn->close();
?>
