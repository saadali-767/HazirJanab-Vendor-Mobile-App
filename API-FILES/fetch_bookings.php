<?php
include("db_config.php");

// Assume $user_id is received from the POST request
$user_id = isset($_POST['user_id']) ? (int)$_POST['user_id'] : 0; // Cast to int for safety
if (!isset($_POST['user_id'])) {
    echo json_encode(array("Status" => 0, "error" => "No user_id provided."));
    exit;
}
try {
    // Modify the SELECT query to match the booking_normal table structure
    // Remove the parentheses around the column names
    $query = "SELECT id, user_id, service_id, vendor_id, city, address, date, time, description, type, status FROM booking_normal WHERE user_id = :user_id";
    $stmt = $dbh->prepare($query);

    // Bind the user_id parameter to the query
    $stmt->bindParam(':user_id', $user_id, PDO::PARAM_INT);

    // Execute the query
    $stmt->execute();
    if ($stmt->errorCode() != '00000') {
        $error = $stmt->errorInfo();
        echo json_encode(array("Status" => 0, "error" => "SQL Error: " . $error[2]));
        exit;
    }
    

    // Fetch all the results
    $result = $stmt->fetchAll(PDO::FETCH_ASSOC);

    // Prepare the response array
    $response = array();

    // Check if any rows are returned
    if (!empty($result)) {
        $response["Status"] = 1; // Status for success
        $response["Bookings"] = $result; // Assign fetched bookings to "Bookings" key
    } else {
        $response["Status"] = 0; // Status for no bookings found
        $response["Bookings"] = array(); // Empty array for "Bookings"
        $response["Message"] = "No bookings found for the provided ID."; // Message key for no bookings
    }

    // Output the response as JSON
    echo json_encode($response);

} catch (PDOException $e) {
    // Catch any error and output as JSON
    echo json_encode(array("Status" => 0, "error" => "Could not execute query. Error: " . $e->getMessage()));
}
?>
