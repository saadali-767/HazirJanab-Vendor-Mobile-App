<?php
ini_set('display_errors', 1);
ini_set('display_startup_errors', 1);
error_reporting(E_ALL);

include("db_config.php");
$response = array();

if(isset($_POST['user_id'])) {
    $user_id = $_POST['user_id'];
    
    $sql = "SELECT * FROM `user` WHERE `id` = '$user_id'";

    $result = mysqli_query($conn, $sql);
    
    if(mysqli_num_rows($result) > 0) {
        // Login successful
        $user = mysqli_fetch_assoc($result); // Fetch user data
        $user['Profile_pic'] = base64_encode($user['Profile_pic']);
        $response['Status'] = 1;
        $response['Message'] = "User Fetched Successfully";
        $response['User'] = $user; // Add user data to response

    } else {
        // Login failed
        $response['Status'] = 0;
        $response['Message'] = "User failed to fetch. No item found with id: $user_id.";
    }
} else {
    // Required fields missing
    $response['Status'] = 0;
    $response['Message'] = "User ID missing.";
}

header('Content-Type: application/json');
echo json_encode($response);

?>
