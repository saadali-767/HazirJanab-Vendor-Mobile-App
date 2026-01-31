<?php
ini_set('display_errors', 1);
ini_set('display_startup_errors', 1);
error_reporting(E_ALL);

include("db_config.php");
$response = array();

if(isset($_POST['email']) && isset($_POST['password'])) {
    $email = $_POST['email'];
    $password = $_POST['password'];
    
    $sql = "SELECT * FROM `user` WHERE `Email` = '$email' AND `Password` = '$password'";

    $result = mysqli_query($conn, $sql);
    
    if(mysqli_num_rows($result) > 0) {
        // Login successful
        $user = mysqli_fetch_assoc($result); // Fetch user data
        if (isset($user['Profile_pic'])) {
            // Convert BLOB data to base64
            $user['Profile_pic'] = base64_encode($user['Profile_pic']);
        }
        $response['Status'] = 1;
        $response['Message'] = "Login successful";
        $response['UserData'] = $user; // Add user data to response

    } else {
        // Login failed
        $response['Status'] = 0;
        $response['Message'] = "Login failed. Please check your credentials.";
    }

} else {
    // Required fields missing
    $response['Status'] = 0;
    $response['Message'] = "Required fields missing";
}

header('Content-Type: application/json');
echo json_encode($response);

?>
