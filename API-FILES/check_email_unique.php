<?php
ini_set('display_errors', 1);
ini_set('display_startup_errors', 1);
error_reporting(E_ALL);

include("db_config.php"); // Your DB configuration file
$response = array();

if(isset($_POST['email'])) {
    $email = $_POST['email'];

    // Prepare a SQL statement to prevent SQL injection
    $stmt = $conn->prepare("SELECT `email` FROM `vendor` WHERE `email` = ?");
    $stmt->bind_param("s", $email); // 's' specifies the variable type => 'string'

    $stmt->execute();
    $result = $stmt->get_result();

    if($result->num_rows == 0) {
        // Email is unique
        $response['Status'] = 1;
        $response['Message'] = "Email is unique and can be used.";
        $response['Email'] = $email;
    } else {
        // Email already exists
        $response['Status'] = 0;
        $response['Message'] = "Email already exists. Please use a different email.";
        $response['Email'] = $email;
    }

    $stmt->close(); // Close statement
} else {
    // Email not provided
    $response['Status'] = 0;
    $response['Message'] = "Email field is missing.";
    $response['Email'] = $email;
}

header('Content-Type: application/json');
echo json_encode($response);
?>
