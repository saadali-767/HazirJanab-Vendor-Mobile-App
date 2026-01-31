<?php
ini_set('display_errors', 1);
ini_set('display_startup_errors', 1);
error_reporting(E_ALL);

include("db_config.php");
$response = array();

if(isset($_POST['booking_id'])) {
    $booking_id = $_POST['booking_id'];
    
    $sql = "SELECT * FROM `booking_normal` WHERE `id` = '$booking_id'";

    $result = mysqli_query($conn, $sql);
    
    if(mysqli_num_rows($result) > 0) {
        // Login successful
        $booking = mysqli_fetch_assoc($result); // Fetch booking data
        $booking['picture'] = base64_encode($booking['picture']);
        $response['Status'] = 1;
        $response['Message'] = "booking Fetched Successfully";
        $response['Booking'] = $booking; // Add booking data to response

    } else {
        // Login failed
        $response['Status'] = 0;
        $response['Message'] = "booking failed to fetch. No item found with id: $booking_id.";
    }
} else {
    // Required fields missing
    $response['Status'] = 0;
    $response['Message'] = "booking ID missing.";
}

header('Content-Type: application/json');
echo json_encode($response);

?>
