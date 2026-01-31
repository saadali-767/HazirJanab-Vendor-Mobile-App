<?php
ini_set('display_errors', 1);
ini_set('display_startup_errors', 1);
error_reporting(E_ALL);

include("db_config.php"); // Your DB configuration file
$response = array();

if(isset($_POST['email'])) {
    $email = $_POST['email'];

    // Check for email uniqueness
    $email_stmt = $conn->prepare("SELECT `email` FROM `vendor` WHERE `email` = ?");
    $email_stmt->bind_param("s", $email); // 's' specifies the variable type => 'string'

    $email_stmt->execute();
    $email_result = $email_stmt->get_result();

    if($email_result->num_rows == 0) {
        // Email is unique
        $response['EmailStatus'] = 1;
        $response['EmailMessage'] = "Email is unique and can be used.";
        $response['Email'] = $email;
    } else {
        // Email already exists
        $response['EmailStatus'] = 0;
        $response['EmailMessage'] = "Email already exists. Please use a different email.";
        $response['Email'] = $email;
    }

    $email_stmt->close(); // Close email statement

    // Check for CNIC uniqueness
    if(isset($_POST['cnic'])) {
        $cnic = $_POST['cnic'];

        $cnic_stmt = $conn->prepare("SELECT `cnic` FROM `vendor` WHERE `cnic` = ?");
        $cnic_stmt->bind_param("s", $cnic); // 's' specifies the variable type => 'string'

        $cnic_stmt->execute();
        $cnic_result = $cnic_stmt->get_result();

        if($cnic_result->num_rows == 0) {
            // CNIC is unique
            $response['CnicStatus'] = 1;
            $response['CnicMessage'] = "CNIC is unique and can be used.";
            $response['Cnic'] = $cnic;
        } else {
            // CNIC already exists
            $response['CnicStatus'] = 0;
            $response['CnicMessage'] = "CNIC already exists. Please use a different CNIC.";
            $response['Cnic'] = $cnic;
        }

        $cnic_stmt->close(); // Close CNIC statement
    } else {
        // CNIC not provided
        $response['CnicStatus'] = 0;
        $response['CnicMessage'] = "CNIC field is missing.";
    }
} else {
    // Email not provided
    $response['EmailStatus'] = 0;
    $response['EmailMessage'] = "Email field is missing.";
}

header('Content-Type: application/json');
echo json_encode($response);
?>
