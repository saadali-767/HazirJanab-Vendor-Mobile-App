<?php
include("db_config.php");

// Retrieve POST parameters
$user_id = isset($_POST['user_id']) ? $_POST['user_id'] : '';
$vendor_id = isset($_POST['vendor_id']) ? $_POST['vendor_id'] : '';

$response = array();

// Check if user_id and vendor_id are provided
if (empty($user_id) || empty($vendor_id)) {
    $response["status"] = "error";
    $response["message"] = "Missing user_id or vendor_id";
    echo json_encode($response);
    exit();
}

// Update booking status to 'Reviewed' where user_id and vendor_id match, and status is 'Complete'
$query = "UPDATE booking_normal SET status='Reviewed' WHERE user_id=? AND vendor_id=? AND status='Complete'";

if ($stmt = $conn->prepare($query)) {
    $stmt->bind_param("ii", $user_id, $vendor_id);
    
    if ($stmt->execute()) {
        $response["status"] = "success";
        $response["message"] = "Booking status updated successfully";
    } else {
        $response["status"] = "error";
        $response["message"] = "Failed to update booking status";
    }
    $stmt->close();
} else {
    $response["status"] = "error";
    $response["message"] = "Database error: could not prepare statement";
}

$conn->close();
echo json_encode($response);
?>
