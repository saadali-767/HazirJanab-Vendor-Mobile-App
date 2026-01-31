<?php
// Database credentials
$servername = "localhost";
$username = "root";
$password = "";
$dbname = "hazirjanab";

// Create connection
$conn = new mysqli($servername, $username, $password, $dbname);

// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

// Get vendor_id from URL parameters
$vendor_id = isset($_GET['vendor_id']) ? intval($_GET['vendor_id']) : 0;

// Prepare and execute the query
$sql = "SELECT description FROM review WHERE vendor_id = ?";
$stmt = $conn->prepare($sql);
$stmt->bind_param("i", $vendor_id);
$stmt->execute();
$result = $stmt->get_result();

// Initialize an array to hold all descriptions
$descriptions = [];

// Fetch all descriptions
while ($row = $result->fetch_assoc()) {
    $descriptions[] = $row['description']; // Add each description to the array
}

// Check if we found any descriptions
if (count($descriptions) > 0) {
    $response = array('descriptions' => $descriptions);
} else {
    // No descriptions found for this vendor_id
    $response = array('descriptions' => 'No descriptions found.');
}

// Close statement and connection
$stmt->close();
$conn->close();

// Set header to return JSON
header('Content-Type: application/json');

// Echo the response
echo json_encode($response);
?>
