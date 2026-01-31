<?php
include("db_config.php"); // Ensure this file contains your PDO database connection setup

// Assume $user_id is received from the POST request
$user_id = isset($_POST['user_id']) ? (int)$_POST['user_id'] : 0; // Cast to int for safety
if ($user_id === 0) {
    echo json_encode(array("Status" => 0, "error" => "No user_id provided."));
    exit;
}

try {
    // Only select vendor_id and status columns from the booking_normal table where user_id matches
    $query = "SELECT vendor_id, status FROM booking_normal WHERE user_id = :user_id";
    $stmt = $dbh->prepare($query);

    // Bind the user_id parameter to the query
    $stmt->bindParam(':user_id', $user_id, PDO::PARAM_INT);

    // Execute the query
    $stmt->execute();

    // Fetch all the results
    $result = $stmt->fetchAll(PDO::FETCH_ASSOC);

    // Prepare the response array
    $response = array();

    // Check if any rows are returned
    if (!empty($result)) {
        $response["Status"] = 1; // Status for success
        $response["Data"] = $result; // Assign fetched data to "Data" key
    } else {
        $response["Status"] = 0; // Status for no data found
        $response["Message"] = "No data found for the provided ID."; // Message key for no data
    }

    // Output the response as JSON
    echo json_encode($response);

} catch (PDOException $e) {
    // Catch any error and output as JSON
    echo json_encode(array("Status" => 0, "error" => "Could not execute query. Error: " . $e->getMessage()));
}
?>
