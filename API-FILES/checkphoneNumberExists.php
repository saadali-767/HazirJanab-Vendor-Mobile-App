<?php
ini_set('display_errors', 1);
ini_set('display_startup_errors', 1);
error_reporting(E_ALL);

include("db_config.php"); // Your DB configuration file
$response = array();

if(isset($_POST['phone_number'])) {
    $number = $_POST['phone_number'];

    // Prepare a SQL statement to prevent SQL injection
    // Modify this line to select the entire row instead of just the phone number
    $stmt = $conn->prepare("SELECT * FROM `vendor` WHERE `phone_number` = ?");
    $stmt->bind_param("s", $number);

    $stmt->execute();
    $result = $stmt->get_result();

    if($result->num_rows == 0) {
        // Number is unique/not found in database
        $response['Status'] = 1;
        $response['Message'] = "Number is unique and not found.";
        $response['phone_number'] = $number;
    } else {
        // Number already exists in the database
        $vendor = $result->fetch_assoc(); // Fetch the matching row as an associative array
        $response['Status'] = 0;
        $response['Message'] = "Number already exists in the database.";
        // Include the vendor data in the response, consider removing sensitive info like passwords
        $response['Vendor'] = $vendor;
    }

    $stmt->close(); // Close statement
} else {
    // Number not provided
    $response['Status'] = 0;
    $response['Message'] = "Number field is missing.";
}

header('Content-Type: application/json');
echo json_encode($response);
?>
