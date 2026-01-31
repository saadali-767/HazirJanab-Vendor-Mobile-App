<?php
include("db_config.php");

// Get parameters from POST request
$user_id = isset($_POST['user_id']) ? (int)$_POST['user_id'] : null;
$vendor_id = isset($_POST['vendor_id']) ? (int)$_POST['vendor_id'] : null;
$booking_id = isset($_POST['booking_id']) ? (int)$_POST['booking_id'] : null;

$response = array();

if ($user_id === null || $vendor_id === null || $booking_id === null) {
    $response["Status"] = 0;
    $response["Message"] = "Missing parameters.";
    echo json_encode($response);
    exit;
}

try {
    $query = "SELECT * FROM review WHERE user_id = :user_id AND vendor_id = :vendor_id AND booking_id = :booking_id";
    $stmt = $dbh->prepare($query);
    $stmt->bindParam(':user_id', $user_id, PDO::PARAM_INT);
    $stmt->bindParam(':vendor_id', $vendor_id, PDO::PARAM_INT);
    $stmt->bindParam(':booking_id', $booking_id, PDO::PARAM_INT);

    $stmt->execute();
    $result = $stmt->fetchAll(PDO::FETCH_ASSOC);

    if (!empty($result)) {
        // Review exists
        $response["Status"] = 1;
        $response["Message"] = "Review exists.";
    } else {
        // No review found
        $response["Status"] = 0;
        $response["Message"] = "No review found.";
    }
} catch (PDOException $e) {
    $response["Status"] = 0;
    $response["Message"] = "Database error: " . $e->getMessage();
}

echo json_encode($response);
?>
