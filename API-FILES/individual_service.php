<?php
ini_set('display_errors', 1);
ini_set('display_startup_errors', 1);
error_reporting(E_ALL);

include("db_config.php");
$response = array();

if(isset($_POST['service_id'])) {
    $service_id = $_POST['service_id'];
    
    $sql = "SELECT * FROM `item` WHERE `id` = '$service_id'";

    $result = mysqli_query($conn, $sql);
    
    if(mysqli_num_rows($result) > 0) {
        // Login successful
        $service = mysqli_fetch_assoc($result); // Fetch user data
        $service['img'] = base64_encode($service['img']);
        $response['Status'] = 1;
        $response['Message'] = "Item Fetched Successfully";
        $response['Service'] = $service; // Add user data to response

    } else {
        // Login failed
        $response['Status'] = 0;
        $response['Message'] = "Item failed to fetch. No item found with id: $service_id.";
    }
} else {
    // Required fields missing
    $response['Status'] = 0;
    $response['Message'] = "Service ID missing.";
}

header('Content-Type: application/json');
echo json_encode($response);

?>
