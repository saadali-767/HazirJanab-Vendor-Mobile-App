<?php
ini_set('display_errors', 1);
ini_set('display_startup_errors', 1);
error_reporting(E_ALL);

include("db_config.php"); // Include your database configuration

$data = json_decode(file_get_contents('php://input'), true);

$response = array();

if (isset($data['mobile_number']) && isset($data['password'])) {
    $mobile_number = $data['mobile_number'];
    $new_password = $data['password'];

    // First, fetch the existing password for the user
    $stmt = $conn->prepare("SELECT `password` FROM `vendor` WHERE `phone_number` = ?");
    $stmt->bind_param("s", $mobile_number);
    $stmt->execute();
    $result = $stmt->get_result();
    
    if ($result->num_rows > 0) {
        $row = $result->fetch_assoc();
        $existing_hashed_password = $row['password'];
        
        // Check if the new password is the same as the old password
        if (password_verify($new_password, $existing_hashed_password)) {
            // New password is the same as the old password
            $response['Status'] = 0;
            $response['Message'] = "New password cannot be the same as the old password.";
        } else {
            // If they're not the same, proceed with updating the password
            $hashed_password = password_hash($new_password, PASSWORD_DEFAULT);
            $update_stmt = $conn->prepare("UPDATE `vendor` SET `password` = ? WHERE `phone_number` = ?");
            $update_stmt->bind_param("ss", $hashed_password, $mobile_number);
            
            if ($update_stmt->execute()) {
                // Password updated successfully
                $response['Status'] = 1;
                $response['Message'] = "Password updated successfully.";
            } else {
                // Error updating password
                $response['Status'] = 0;
                $response['Message'] = "Error updating password.";
            }
            $update_stmt->close();
        }
    } else {
        // Mobile number does not exist
        $response['Status'] = 0;
        $response['Message'] = "Mobile number does not exist.";
    }

    $stmt->close();
} else {
    // Mobile number or password not provided
    $response['Status'] = 0;
    $response['Message'] = "Mobile number or password field is missing.";
}

header('Content-Type: application/json');
echo json_encode($response);
?>
