<?php
include("db_config.php");

try {
    // Check if vendor_id parameter is provided in the request
    if (!isset($_POST['booking_id'])) {
        echo json_encode(array("Status" => 0, "error" => "Booking ID not provided."));
        exit;
    }

    $bookingId = $_POST['booking_id'];

    // Prepare the SQL query to update the status of the booking
    $query = "UPDATE booking_normal SET status = 'Accepted' WHERE id = :booking_id";
    $stmt = $dbh->prepare($query);

    $stmt->bindParam(':booking_id', $bookingId, PDO::PARAM_INT);

    // Execute the query
    $stmt->execute();

    // Check if any rows were affected
    if ($stmt->rowCount() > 0) {
        echo json_encode(array("Status" => 1, "Message" => "Booking status updated to 'accepted' for booking ID: $bookingId"));
    } else {
        echo json_encode(array("Status" => 0, "Message" => "No matching booking found for booking ID: $bookingId"));
    }

} catch (PDOException $e) {
    // Catch any error and output as JSON
    echo json_encode(array("Status" => 0, "Message" => "Could not execute query. Error: " . $e->getMessage()));
}
?>
